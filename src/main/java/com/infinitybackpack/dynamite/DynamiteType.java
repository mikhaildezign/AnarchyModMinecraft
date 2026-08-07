package com.infinitybackpack.dynamite;

public enum DynamiteType {
    VANILLA(4.0f),          // Обычный ТНТ
    DYNAMITE_A(12.0f),      // x3 ванильного ТНТ
    DYNAMITE_B(40.0f),      // x10 ванильного ТНТ
    DYNAMITE_B2(0.0f),      // Куб 24x24x24
    DYNAMITE_C4(2.5f),      // Ступенчатый пробой
    SHOCKWAVE(2.5f),        // Улучшенный C4, под водой
    STILLER(0.0f),          // Только спавнеры, 50% шанс
    RELIABLE_STILLER(0.0f); // Только спавнеры, 75% шанс

    private final float baseRadius;

    DynamiteType(float baseRadius) {
        this.baseRadius = baseRadius;
    }

    public float getBaseRadius() {
        return baseRadius;
    }
}