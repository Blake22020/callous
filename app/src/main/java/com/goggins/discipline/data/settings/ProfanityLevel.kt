package com.goggins.discipline.data.settings

/** Уровень нецензурной лексики. Настраивается в Settings, уважает чувствительных людей. */
enum class ProfanityLevel(val title: String, val subtitle: String) {
    CLEAN(
        "БЕЗ МАТА // CLEAN",
        "Прямо и требовательно, без мата и ругани."
    ),
    MILD(
        "НЕМНОГО ПЕРЦА // MILD",
        "Лёгкий мат: «чёрт», «задница». Пинки и похвала тоже с перцем."
    ),
    SAVAGE(
        "ЖЕСТЬ // SAVAGE",
        "Много мата и резкие выпады за отмазки и пропуски."
    ),
    NIGHTMARE(
        "КОШМАР // NIGHTMARE",
        "Максимум мата и прямых оскорблений. Включая напоминания."
    );

    companion object {
        fun fromOrdinal(o: Int): ProfanityLevel = entries.getOrElse(o) { MILD }
    }
}
