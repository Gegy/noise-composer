package dev.gegy.noise.compile;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.BitSet;

public final class LocalAllocator {
    private final BitSet locals = new BitSet();

    private LocalAllocator() {
    }

    public static LocalAllocator forStatic() {
        return new LocalAllocator();
    }

    public static LocalAllocator forInstance(Type selfType) {
        LocalAllocator allocator = new LocalAllocator();
        allocator.allocate(selfType);
        return allocator;
    }

    public LocalRef allocate(Type type) {
        int size = type.getSize();
        int slotIdx = this.getFreeSlotFor(size);

        LocalRef local = new LocalRef(this, slotIdx, type);

        for (int i = 0; i < size; i++) {
            this.locals.set(slotIdx + i, true);
        }

        return local;
    }

    public LocalRef copiedFrom(MethodVisitor method, ValueRef value) {
        LocalRef local = this.allocate(value.getType());
        value.load(method);
        local.store(method);
        return local;
    }

    private int getFreeSlotFor(int size) {
        BitSet locals = this.locals;

        int fromIdx = 0;
        while (true) {
            int clearBit = locals.nextClearBit(fromIdx);
            if (this.canFitAt(clearBit, size)) {
                return clearBit;
            } else {
                fromIdx = clearBit + size;
            }
        }
    }

    private boolean canFitAt(int start, int size) {
        for (int i = start; i < start + size; i++) {
            if (this.locals.get(i)) {
                return false;
            }
        }
        return true;
    }

    void release(LocalRef local) {
        int minSlot = local.index;
        int maxSlot = local.index + local.type.getSize();

        for (int slot = minSlot; slot < maxSlot; slot++) {
            this.locals.set(slot, false);
        }
    }
}
