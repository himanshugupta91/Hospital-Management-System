package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital";

    private static final String userName = "newuser";

    private static final String password = "himanshu@1";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            Patient patient= new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctor");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                        case 2:
                            patient.viewPatient();
                            System.out.println();
                            break;
                            case 3:
                                doctor.viewDoctor();
                                System.out.println();
                                break;
                                case 4:
                                    bookAppointment(patient,doctor,connection,scanner);
                                    System.out.println();
                                    break;
                                    case 5:
                                        System.out.println("Thank you for using Hospital Management System");
                                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }


    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD): ");
        String date = scanner.next();
        if (patient.getPatientByID(patientId) && doctor.getDoctorByID(doctorId)){
                if (checkDoctorAvailability(doctorId, date,connection)){
                    String appointmentQuery = "INSERT INTO appointments (patient_id, doctor_id, date) VALUES (?,?,?)";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                        preparedStatement.setInt(1, patientId);
                        preparedStatement.setInt(2, doctorId);
                        preparedStatement.setString(3, date);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Appointment Booked Successfully");
                        } else {
                            System.out.println("Appointment Booking Failed");
                        }
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Doctor is not available");
                }
        } else {
            System.out.println("Patient or Doctor does not exist");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String date, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }

}
