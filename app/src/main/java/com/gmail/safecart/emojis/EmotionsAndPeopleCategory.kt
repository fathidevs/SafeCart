package com.gmail.safecart.emojis

class EmotionsAndPeopleCategory {

    companion object {
        fun epEmojiNameAndCodeList(): ArrayList<EmojiModel> {
            val returnList = ArrayList<EmojiModel>()
            returnList.addAll(
                listOf(
                    EmojiModel("Shopping", 0x1F6CD),
                    EmojiModel("Groceries", 0x1F600),
                    EmojiModel("Hygiene", 0x1F600),
                    EmojiModel("Electronics", 0x1F600),
                    EmojiModel("Fun", 0x1F600),
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