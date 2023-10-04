import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EmployeeAnalyzer {

    public static void main(String[] args) {
        String filePath = "Assignment_Timecard.xlsx - Sheet1.tsv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {    // Skip processing the header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                //Take out the data from the file
                String[] employeeData = line.split("\t");
                String positionID = employeeData[0];
                String positionStatus = employeeData[1];
                String timeIn = employeeData[2];
                String timeOut = employeeData[3];
                String timecardHours = employeeData[4];
                String employeeName = employeeData[7];

                // Keep track of dates the employee is present
                Set<String> presentDates = new HashSet<>();

                if (!timecardHours.isEmpty() && timecardHours.matches("\\d{1,2}:\\d{2}")) {
                    String[] timeParts = timecardHours.split(":");
                    int totalMinutes = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);


                    if (!timeIn.isEmpty() && !timeOut.isEmpty()) {

                        // Check conditions
                        String date = timeIn.split(" ")[0];
                        presentDates.add(date);

                        // Check for 7 consecutive days of presence
                        if (hasConsecutiveDays(presentDates)) {
                            System.out.println(employeeName + " was present for 7 consecutive days.");
                        }

                        long diffInMinutes = getMinutesDifference(timeIn, timeOut);

                        if (diffInMinutes > 60 && diffInMinutes < 600) {
                            System.out.println(employeeName + " has less than 10 hours between shifts.");
                        }

                        //Check for work for more than 14 hours per shift
                        if (totalMinutes > 14 * 60) {
                            System.out.println(employeeName + " has worked for more than 14 hours in a single shift.");
                        }
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to calculate time difference in minutes
    private static long getMinutesDifference(String timeIn, String timeOut) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        try {
            Date dateIn = format.parse(timeIn);
            Date dateOut = format.parse(timeOut);

            long diffInMilliseconds = dateOut.getTime() - dateIn.getTime();
            return diffInMilliseconds / (60 * 1000); // Convert milliseconds to minutes
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Helper method to check for 7 consecutive days
    private static boolean hasConsecutiveDays(Set<String> dates) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        int consecutiveCount = 0;
        String prevDate = null;

        for (String date : dates) {
            if (prevDate != null) {
                try {
                    Date currentDate = format.parse(date);
                    Date prevDateParsed = format.parse(prevDate);
                    long diffInDays = (currentDate.getTime() - prevDateParsed.getTime()) / (1000 * 60 * 60 * 24);

                    if (diffInDays == 1) {
                        consecutiveCount++;
                        if (consecutiveCount >= 7) {
                            return true;
                        }
                    } else {
                        consecutiveCount = 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            prevDate = date;
        }

        return false;
    }
}
