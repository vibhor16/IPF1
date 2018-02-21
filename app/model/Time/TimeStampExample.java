package model.Time;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shuklv1 on 04-Oct-17.
 */
public class TimeStampExample {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public String timeStamp() {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return (readable(sdf.format(timestamp).toString()));

    }

    public String readable(String timestamp) {
        String parts[] = timestamp.split("\\.");
        String readableTimeStamp = "";
        String month = "";
        switch (Integer.parseInt(parts[1].trim())) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }

        readableTimeStamp += parts[2] + " " + month + " " + parts[0] + " " + parts[3] + "." + parts[4] + "." + parts[5];
        return readableTimeStamp;
    }


}
