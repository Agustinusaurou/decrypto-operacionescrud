package com.decrypto.operacionescrud.Utils;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class Either<L, R> {

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> left(L left) {
        validateNotNull(left, "Left cannot be null");
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> right(R right) {
        validateNotNull(right, "Right cannot be null");
        return new Either<>(null, right);
    }

    public boolean isLeft() {
        return this.left != null;
    }

    public boolean isRight() {
        return this.right != null;
    }

    public L getLeft() {
        validateOperation(this::isRight, "Trying to get Left when Either is Right");
        return this.left;
    }

    public R getRight() {
        validateOperation(this::isLeft, "Trying to get Right when Either is Left");
        return this.right;
    }

    private static <E> void validateNotNull(E value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void validateOperation(BooleanSupplier is, String message) {
        if (is.getAsBoolean()) {
            throw new UnsupportedOperationException(message);
        }
    }
}
