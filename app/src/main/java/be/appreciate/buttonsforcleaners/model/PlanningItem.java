package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;
import android.location.Location;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.ContractTypeTable;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;
import be.appreciate.buttonsforcleaners.database.PlanningTable;
import be.appreciate.buttonsforcleaners.utils.DateUtils;
import be.appreciate.buttonsforcleaners.utils.LocationUtils;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class PlanningItem
{
    private int id;
    private int contractTypeId;
    private String contractTypeName;
    private String accountancyName;
    private String customerName;
    private long date;
    private String startTime;
    private String endTime;
    private String locationName;
    private boolean proximityRequired;
    private String description;
    private String instructions;
    private String phone;
    private String street;
    private String postalCode;
    private String city;
    private double latitude;
    private double longitude;
    private long realStartTime;
    private long realEndTime;
    private boolean startCompleted;
    private boolean endCompleted;
    private boolean sentToApi;
    private int questionCountAtStart;
    private int questionCountAtEnd;

    private static final int MAX_DISTANCE_TO_DESTINATION = 500;

    public static List<PlanningItem> constructListFromCursor(Cursor cursor)
    {
        List<PlanningItem> planningItems = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                planningItems.add(PlanningItem.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return planningItems;
    }

    public static PlanningItem constructFromCursor(Cursor cursor)
    {
        PlanningItem planningItem = new PlanningItem();

        int startColumnIndex = cursor.getColumnIndex(PlanningTable.COLUMN_QUESTIONS_AT_START_FULL);
        int endColumnIndex = cursor.getColumnIndex(PlanningTable.COLUMN_QUESTIONS_AT_STOP_FULL);

        planningItem.id = cursor.getInt(cursor.getColumnIndex(PlanningTable.COLUMN_PLANNING_ID_FULL));
        planningItem.contractTypeId = cursor.getInt(cursor.getColumnIndex(ContractTypeTable.COLUMN_CONTRACT_TYPE_ID_FULL));
        planningItem.contractTypeName = cursor.getString(cursor.getColumnIndex(ContractTypeTable.COLUMN_NAME_FULL));
        planningItem.accountancyName = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_ACCOUNTANCY_NAME_FULL));
        planningItem.customerName = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_CUSTOMER_NAME_FULL));
        planningItem.date = cursor.getLong(cursor.getColumnIndex(PlanningTable.COLUMN_DATE_FULL));
        planningItem.startTime = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_START_TIME_FULL));
        planningItem.endTime = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_END_TIME_FULL));
        planningItem.locationName = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_LOCATION_NAME_FULL));
        planningItem.proximityRequired = cursor.getInt(cursor.getColumnIndex(PlanningTable.COLUMN_PROXIMITY_REQUIRED_FULL)) == 1;
        planningItem.description = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_DESCRIPTION_FULL));
        planningItem.instructions = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_INSTRUCTIONS_FULL));
        planningItem.phone = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_PHONE_FULL));
        planningItem.street = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_STREET_FULL));
        planningItem.postalCode = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_POSTAL_CODE_FULL));
        planningItem.city = cursor.getString(cursor.getColumnIndex(PlanningTable.COLUMN_CITY_FULL));
        planningItem.latitude = cursor.getDouble(cursor.getColumnIndex(PlanningTable.COLUMN_LATITUDE_FULL));
        planningItem.longitude = cursor.getDouble(cursor.getColumnIndex(PlanningTable.COLUMN_LONGITUDE_FULL));
        planningItem.realStartTime = cursor.getLong(cursor.getColumnIndex(FeedbackTable.COLUMN_START_TIME_FULL));
        planningItem.realEndTime = cursor.getLong(cursor.getColumnIndex(FeedbackTable.COLUMN_END_TIME_FULL));
        planningItem.startCompleted = cursor.getInt(cursor.getColumnIndex(FeedbackTable.COLUMN_START_COMPLETED_FULL)) == 1;
        planningItem.endCompleted = cursor.getInt(cursor.getColumnIndex(FeedbackTable.COLUMN_END_COMPLETED_FULL)) == 1;
        planningItem.sentToApi = cursor.getInt(cursor.getColumnIndex(FeedbackTable.COLUMN_SENT_TO_API_FULL)) == 1;
        planningItem.questionCountAtStart = startColumnIndex >= 0 ? cursor.getInt(startColumnIndex) : 0;
        planningItem.questionCountAtEnd = endColumnIndex >= 0 ? cursor.getInt(endColumnIndex) : 0;

        return planningItem;
    }

    public int getId()
    {
        return id;
    }

    public int getContractTypeId()
    {
        return contractTypeId;
    }

    public String getContractTypeName()
    {
        return contractTypeName;
    }

    public String getAccountancyName()
    {
        return accountancyName;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public long getDate()
    {
        return date;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public boolean isProximityRequired()
    {
        return this.proximityRequired && (this.latitude != 0 || this.longitude != 0);
    }

    public String getDescription()
    {
        return description;
    }

    public String getInstructions()
    {
        return instructions;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getStreet()
    {
        return street;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getCity()
    {
        return city;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public long getRealStartTime()
    {
        return realStartTime;
    }

    public long getRealEndTime()
    {
        return realEndTime;
    }

    public boolean isStartCompleted()
    {
        return startCompleted;
    }

    public boolean isEndCompleted()
    {
        return endCompleted;
    }

    public boolean isSentToApi()
    {
        return sentToApi;
    }

    public int getQuestionCountAtStart()
    {
        return questionCountAtStart;
    }

    public int getQuestionCountAtEnd()
    {
        return questionCountAtEnd;
    }

    public int getQuestionCount()
    {
        return this.startCompleted ? this.questionCountAtEnd : this.questionCountAtStart;
    }

    public boolean canStartExecution(Location location)
    {
        float distanceToDestination = LocationUtils.getDistanceBetween(location, this.latitude, this.longitude);
        boolean locationHasCoordinates = this.latitude != 0 || this.longitude != 0;
        return !this.proximityRequired || !locationHasCoordinates || (distanceToDestination >= 0 && distanceToDestination < MAX_DISTANCE_TO_DESTINATION);
    }

    public String getAddress()
    {
        boolean addStreetSeparator = false;
        boolean addPostalCodeSeparator = false;
        StringBuilder sb = new StringBuilder();

        if(!TextUtils.isEmpty(this.street))
        {
            sb.append(this.street);
            addStreetSeparator = true;
        }

        if(!TextUtils.isEmpty(this.postalCode))
        {
            if(addStreetSeparator)
            {
                sb.append(", ");
                addStreetSeparator = false;
            }
            sb.append(this.postalCode);
            addPostalCodeSeparator = true;
        }

        if(!TextUtils.isEmpty(this.city))
        {
            if(addStreetSeparator)
            {
                sb.append(", ");
                addStreetSeparator = false;
            }
            if(addPostalCodeSeparator)
            {
                sb.append(" ");
                addPostalCodeSeparator = false;
            }
            sb.append(this.city);
        }

        return sb.toString();
    }

    public String getFormattedDate()
    {
        return this.date > 0 ? DateUtils.formatDetailDate(this.date) : null;
    }
}
