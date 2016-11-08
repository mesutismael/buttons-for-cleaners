package be.appreciate.buttonsforcleaners.model;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public enum QuestionType
{
    TEXT_SHORT,
    TEXT_LONG,
    PHOTOS,
    BOOLEAN,
    SINGLE_CHOICE,
    MULTI_CHOICE,
    DRAWING,
    PRODUCT_PICKER;

    public static QuestionType getQuestionType(int questionTypeId)
    {
        switch (questionTypeId)
        {
            case 0:
                return TEXT_SHORT;

            case 1:
                return TEXT_LONG;

            case 2:
                return PHOTOS;

            case 3:
                return BOOLEAN;

            case 4:
                return SINGLE_CHOICE;

            case 5:
                return MULTI_CHOICE;

            case 6:
                return DRAWING;

            case 7:
                return PRODUCT_PICKER;

            default:
                return TEXT_SHORT;
        }
    }
}
