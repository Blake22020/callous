package com.goggins.discipline.data.settings

/** Уровень нецензурной лексики. Настраивается в Settings, уважает чувствительных людей. */
enum class ProfanityLevel(val title: String, val subtitle: String) {
    CLEAN(
        "БЕЗ МАТА // CLEAN",
        "Жёстко, но без мата. Для чувствительных. Гоггинс всё равно недоволен."
    ),
    MILD(
        "НЕМНОГО ПЕРЦА // MILD",
        "Лёгкие «чёрт», «задница». Щадит уши, бьёт по самолюбию."
    ),
    SAVAGE(
        "ЖЕСТЬ // SAVAGE",
        "Умеренный мат. Как Гоггинс в интервью. Больно, но честно."
    ),
    NIGHTMARE(
        "КОШМАР // NIGHTMARE",
        "Очень много мата. Полный Гоггинс. Только для толстокожих."
    );

    companion object {
        fun fromOrdinal(o: Int): ProfanityLevel = entries.getOrElse(o) { MILD }
    }
}
