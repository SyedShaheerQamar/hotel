package org.hotel.Service;

import org.hotel.DAO.BookingDAO;
import org.hotel.DAO.CustomerDAO;
import org.hotel.DAO.HotelDAO;
import org.hotel.DAO.RoomDAO;
import org.hotel.Domain.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private final ReportService reportService = new ReportService();

    BookingDAO dao = new BookingDAO();

    public String getTotalPrice(String adate, String ddate, Integer h_id){
        List<Bill> bookingList = dao.getTotalPrice(h_id, adate, ddate);

        Integer price = bookingList.get(0).getPrice();
        HotelDAO hotelDAO  = new HotelDAO();
        Hotel hotel = hotelDAO.getById(Long.valueOf(h_id));

        return "The  total  bill  of  Hotel   : "+hotel.getHotel_name()+"  is  "+price;
    }

    public String[][] MonthlyReportBooking(String adate, String ddate, Integer id){
        List<Booking> bookingList = dao.getMonthlyReportBooking(adate, ddate, id);
//        bookingList.forEach(System.out::println);

        return convertValuesIntoJTable(bookingList, 8);
    }

    public Boolean updateValueOfBooking(Integer id){
        Booking booking = dao.getById(Long.valueOf(id));

        if(booking.getBooking_status().equalsIgnoreCase("complete")){
            return Boolean.TRUE;
        }
        LocalDate now = LocalDate.now();

        booking.setBooking_status("complete");
        dao.updateAllValues(booking, id);
        return Boolean.FALSE;
    }

    public Boolean deleteValueOfBooking(Integer id){
        Booking booking = dao.getById(Long.valueOf(id));

        if(booking.getBooking_status().equalsIgnoreCase("inactive")){
            return Boolean.TRUE;
        }
        LocalDate now = LocalDate.now();

        booking.setBooking_status("inactive");
        dao.updateAllValues(booking, id);
        return Boolean.FALSE;
    }

    public Booking getBookingById(Integer id){

        return dao.getById(Long.valueOf(id));

    }

    public void deleteById(String id){

        dao.deleteById(Long.valueOf(id));

    }

    public String[] checkAvailability(Integer id, String adate, String ddate){
        RoomDAO roomDAO = new RoomDAO();
        List<Room> roomList = roomDAO.roomAvailability(id, adate, ddate);

        if(roomList.size() == 0){
            String[] data = {"None"};
            return data;
        }
        else {
            return convertValuesToComboBoxRoom(roomList, 6);
        }
    }

    public String[][] searchByNameInBooking(String name){

        List<Booking> bookingList = dao.searchByName(name);
        return convertValuesIntoJTable(bookingList, 8);

    }

    public void insertIntoBooking(Integer hID, Integer rID, Integer cID, String aDate, String dDate){
        RoomDAO roomDAO = new RoomDAO();
        Room room = roomDAO.getById(Long.valueOf(rID));

        Booking booking = Booking.builder()
                .hotel_id(hID)
                .room_id(rID)
                .customer_id(cID)
                .price(room.getRoom_price())
                .arrival_date(aDate)
                .departure_date(dDate)
                .booking_status("active")
                .build();

        dao.insert(booking);
    }

    public String[][] getAllValuesOfBooking(){

        List<Booking> bookingList = dao.getAllBooking();
        return convertValuesIntoJTable(bookingList, 8);
    }

    public String[] getValuesHotel(){
        HotelDAO hotelDAO = new HotelDAO();
        List<Hotel> hotelList = hotelDAO.getAll();

        return convertValuesToComboBoxHotel(hotelList, 5);
    }

    public String[] getValuesCustomer(){
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customerList = customerDAO.getAll();

        return convertValuesToComboBoxCustomer(customerList, 5);
    }

    private String[][] convertValuesIntoJTable(List<Booking> bookingList, int columnSize){

        String[][] data = new String[bookingList.size()][columnSize];

        LocalDate now = LocalDate.now();

        for(int i=0; i<bookingList.size(); i++){
            data[i][0] = String.valueOf(bookingList.get(i).getId());
            data[i][1] = reportService.getHotelName(bookingList.get(i).getHotel_id());
            data[i][2] = reportService.getRoomName(bookingList.get(i).getRoom_id());
            data[i][3] = reportService.getCustomerName(bookingList.get(i).getCustomer_id());
            data[i][4] = String.valueOf(bookingList.get(i).getPrice());
            data[i][5] = bookingList.get(i).getArrival_date();
            String adateValue = data[i][5];
            String[] adate = adateValue.split("-");
            Integer year = Integer.valueOf(adate[0]);
            Integer month = Integer.valueOf(adate[1]);
            Integer day = Integer.valueOf(adate[2]);
            LocalDate arrDate = LocalDate.of(year, month, day);
            data[i][6] = bookingList.get(i).getDeparture_date();
            String dateValue = data[i][6];
            String[] date = dateValue.split("-");
            Integer dyear = Integer.valueOf(date[0]);
            Integer dmonth = Integer.valueOf(date[1]);
            Integer dday = Integer.valueOf(date[2]);
            LocalDate depDate = LocalDate.of(dyear, dmonth, dday);
            if(depDate.isBefore(now)){
                data[i][7] = "complete";
            }
            else {
                data[i][7] = bookingList.get(i).getBooking_status();
            }
        }

        return data;
    }

    public String[] convertValuesToComboBoxHotel(List<Hotel> hotelList, Integer columnSize){

        String[][] data = new String[hotelList.size()][columnSize];
        String[] value = new String[hotelList.size()];

        for(int i=0; i<hotelList.size(); i++){
            data[i][0] = String.valueOf(hotelList.get(i).getId());
            data[i][1] = hotelList.get(i).getHotel_name();

            value[i] = data[i][0] + ",  "+ data[i][1];
        }

        return value;

    }

    public String[] convertValuesToComboBoxRoom(List<Room> roomList, Integer columnSize){

        String[][] data = new String[roomList.size()][columnSize];
        String[] value = new String[roomList.size()];

        for(int i=0; i<roomList.size(); i++){
            data[i][0] = String.valueOf(roomList.get(i).getId());
            data[i][1] = roomList.get(i).getRoom_floor();
            data[i][2] = roomList.get(i).getCategory();

            value[i] = data[i][0] + ",  "+ data[i][1] +",  "+ data[i][2];
        }

//        String[] test = new String[roomList.size()];
//
//        int count  = 0;
//        for(Room r : roomList){
//            test[count] = String.valueOf(r.getId()+", "+r.getCategory());
//            count++;
//        }
//
//        for (int i=0; i<test.length; i++){
//            System.out.println(test[i]);
//        }

        return value;

    }

    public String[] convertValuesToComboBoxCustomer(List<Customer> customerList, Integer columnSize){

        String[][] data = new String[customerList.size()][columnSize];
        String[] value = new String[customerList.size()];

        for(int i=0; i<customerList.size(); i++){
            data[i][0] = String.valueOf(customerList.get(i).getId());
            data[i][1] = customerList.get(i).getName();

            value[i] = data[i][0] + ",  "+ data[i][1];
        }

        return value;

    }

}
