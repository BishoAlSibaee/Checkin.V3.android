package hotelservices.syriasoft.cleanup;

import java.util.List;

public class Notification {

    int reqCode;
    String room;
    String title;
    String message;

    public Notification(int reqCode, String room,String title,String message) {
        this.reqCode = reqCode;
        this.room = room;
        this.message = message;
        this.title = title;
    }

    public static Notification searchNotification(List<Notification> list,String room,String order) {
        for (Notification n:list) {
            if (n.room.equals(room) && Notification.getNotificationOrder(n.title).equals(order)) {
                return n;
            }
        }
        return null;
    }

    public static String getNotificationOrder(String title) {
        if (title.contains("Cleanup")) {
            return "Cleanup";
        }
        else if (title.contains("Laundry")) {
            return "Laundry";
        }
        else if (title.contains("RoomService")) {
            return "RoomService";
        }
        else if (title.contains("SOS")) {
            return "SOS";
        }
        else {
            return "";
        }
    }

    public static boolean getNotificationType(String message) {
        if (message.contains("New")) {
            return true;
        }
        else if (message.contains("cancelled")) {
            return false;
        }
        else {
            return false;
        }
    }

    public static boolean removeNotification(List<Notification> list,int reqCode) {
        for (Notification n :list) {
            if (n.reqCode == reqCode) {
                return list.remove(n);
            }
        }
        return false;
    }
}
