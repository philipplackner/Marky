package com.plcoding.marky.core.designsystem.icon

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Outline icons of the Marky design system: 24 grid, 1.8 stroke, round caps and joins.
 * Path data comes straight from `designs/highlighter/site.js`.
 *
 * Draw them with `Icon` so they are tinted with the content color (`ink` by default).
 */
object MarkyIcons {
    /** Back: chevron on iOS, arrow on Android. */
    val Back: ImageVector
        get() = if (usesIosIcons) BackChevron else BackArrow

    /** Overflow: horizontal dots on iOS, vertical dots on Android. */
    val More: ImageVector
        get() = if (usesIosIcons) MoreHorizontal else MoreVertical

    /** Search. */
    val Search: ImageVector by lazy {
        strokeIcon(
            "Search",
            "M4.5 11a6.5 6.5 0 1 0 13 0a6.5 6.5 0 1 0 -13 0z",
            "M16 16l4.5 4.5",
        )
    }

    /** Import a .md file. */
    val Import: ImageVector by lazy {
        strokeIcon(
            "Import",
            "M12 4v10M8 10l4 4 4-4",
            "M4 15v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3",
        )
    }

    /** Export as .md. */
    val Export: ImageVector by lazy {
        strokeIcon(
            "Export",
            "M12 15V4M8 8l4-4 4 4",
            "M4 15v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3",
        )
    }

    /** New note. */
    val Plus: ImageVector by lazy {
        strokeIcon(
            "Plus",
            "M12 5v14M5 12h14",
        )
    }

    /** Pin a note; also the pin mark on tiles. */
    val Pin: ImageVector by lazy {
        strokeIcon(
            "Pin",
            "M9 4h6l-1 6 3 3v1H7v-1l3-3z",
            "M12 14v6",
        )
    }

    /** Unpin a note. */
    val PinOff: ImageVector by lazy {
        strokeIcon(
            "PinOff",
            "M9 4h6l-1 6 3 3v1H7v-1l3-3z",
            "M12 14v6",
            "M4 4l16 16",
        )
    }

    /** Back on iOS. Prefer [Back], which picks the platform icon. */
    val BackChevron: ImageVector by lazy {
        strokeIcon(
            "BackChevron",
            "M15 5l-7 7 7 7",
        )
    }

    /** Back on Android. Prefer [Back], which picks the platform icon. */
    val BackArrow: ImageVector by lazy {
        strokeIcon(
            "BackArrow",
            "M20 12H5M11 6l-6 6 6 6",
        )
    }

    /** Overflow on Android. Prefer [More], which picks the platform icon. */
    val MoreVertical: ImageVector by lazy {
        strokeIcon(
            "MoreVertical",
            "M10.9 5.5a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
            "M10.9 12a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
            "M10.9 18.5a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
        )
    }

    /** Overflow on iOS. Prefer [More], which picks the platform icon. */
    val MoreHorizontal: ImageVector by lazy {
        strokeIcon(
            "MoreHorizontal",
            "M4.4 12a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
            "M10.9 12a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
            "M17.4 12a1.1 1.1 0 1 0 2.2 0a1.1 1.1 0 1 0 -2.2 0z",
        )
    }

    /** Close. */
    val Close: ImageVector by lazy {
        strokeIcon(
            "Close",
            "M6 6l12 12M18 6L6 18",
        )
    }

    /** Clear the search field. */
    val Clear: ImageVector by lazy {
        strokeIcon(
            "Clear",
            "M3.5 12a8.5 8.5 0 1 0 17 0a8.5 8.5 0 1 0 -17 0z",
            "M9.2 9.2l5.6 5.6M14.8 9.2l-5.6 5.6",
        )
    }

    /** Delete a note. */
    val Trash: ImageVector by lazy {
        strokeIcon(
            "Trash",
            "M4.5 7h15M10 4h4M6.5 7l1 12a1.5 1.5 0 0 0 1.5 1.4h6a1.5 1.5 0 0 0 1.5-1.4l1-12",
            "M10 11v6M14 11v6",
        )
    }

    /** Bullet list tool. */
    val BulletList: ImageVector by lazy {
        strokeIcon(
            "BulletList",
            "M9 6h11M9 12h11M9 18h11",
            "M3.6 6a0.9 0.9 0 1 0 1.8 0a0.9 0.9 0 1 0 -1.8 0z",
            "M3.6 12a0.9 0.9 0 1 0 1.8 0a0.9 0.9 0 1 0 -1.8 0z",
            "M3.6 18a0.9 0.9 0 1 0 1.8 0a0.9 0.9 0 1 0 -1.8 0z",
        )
    }

    /** Checklist tool. */
    val Checklist: ImageVector by lazy {
        strokeIcon(
            "Checklist",
            "M8.5 3.5h7a5 5 0 0 1 5 5v7a5 5 0 0 1 -5 5h-7a5 5 0 0 1 -5 -5v-7a5 5 0 0 1 5 -5z",
            "M8 12l3 3 5-6",
        )
    }

    /** Quote tool. */
    val Quote: ImageVector by lazy {
        strokeIcon(
            "Quote",
            "M5 5v14M10 8h9M10 12h9M10 16h6",
        )
    }

    /** Code tool. */
    val Code: ImageVector by lazy {
        strokeIcon(
            "Code",
            "M8 7l-5 5 5 5M16 7l5 5-5 5",
        )
    }

    /** Link tool. */
    val Link: ImageVector by lazy {
        strokeIcon(
            "Link",
            "M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1",
            "M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1",
        )
    }

    /** Check mark. */
    val Check: ImageVector by lazy {
        strokeIcon(
            "Check",
            "M5 12.5l4.5 4.5L19 7.5",
        )
    }

    /** Alert. */
    val Alert: ImageVector by lazy {
        strokeIcon(
            "Alert",
            "M3.5 12a8.5 8.5 0 1 0 17 0a8.5 8.5 0 1 0 -17 0z",
            "M12 7.5v5.5M12 16.3v.2",
        )
    }

    /** Markdown file. */
    val File: ImageVector by lazy {
        strokeIcon(
            "File",
            "M14 3.5H7a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8.5z",
            "M14 3.5v5h5",
            "M8.5 16.5v-4l2 2 2-2v4M15.5 12.5v4M14 15l1.5 1.5L17 15",
        )
    }
}
