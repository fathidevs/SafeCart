package com.gmail.safecart.emojis

class InitialCategoryPicks {
    companion object {
        fun epEmojiNameAndCodeList(): ArrayList<EmojiModel> {
            val returnList = ArrayList<EmojiModel>()
            returnList.addAll(
                listOf(
                    EmojiModel("Shopping", 0x1F6D2),
                    EmojiModel("Groceries", 0x1F345),
                    EmojiModel("Hygiene", 0x1F9FC),
                    EmojiModel("Electronics", 0x1F50C),
                    EmojiModel("Fun", 0x1F973),
                    EmojiModel("Custom", 0x2795),
                )
            )
            return returnList
        }

        private fun epSize(): Int {
            return epEmojiNameAndCodeList().size
        }
    }
}