package dev.finalproject.data;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import dev.finalproject.App;
import dev.finalproject.models.Cluster;
import dev.finalproject.models.SchoolYear;
import dev.finalproject.models.Student;
import dev.sol.db.DBParam;
import dev.sol.db.DBService;
import dev.sol.db.DBType;
import javafx.collections.ObservableList;

public class StudentDAO {

    public static final String TABLE = "student";
    public static final DBService DB = App.DB_SMS;

    private static ObservableList<Cluster> CLUSTER_LIST;
    private static ObservableList<SchoolYear> SCHOOL_YEAR_LIST;

    public static void initialize(ObservableList<Cluster> clusterList, ObservableList<SchoolYear> schoolYearList) {
        CLUSTER_LIST = clusterList;
        SCHOOL_YEAR_LIST = schoolYearList;
    }

    public static Student data(CachedRowSet crs) {

        try {

            Cluster clusterID = CLUSTER_LIST.stream()
                    .filter(cluster -> {
                        try {
                            return cluster.getClusterID() == (crs.getInt("clusterID"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }).findFirst().get();

            SchoolYear yearID = SCHOOL_YEAR_LIST.stream()
                    .filter(year -> {
                        try {
                            return year.getYearID() == (crs.getInt("yearID"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }).findFirst().get();

            int id = crs.getInt("StudentID");
            String firstName = crs.getString("FirstName");
            String middleName = crs.getString("MiddleName");
            String lastName = crs.getString("LastName");
            String nameExtension = crs.getString("NameExtension");
            String email = crs.getString("Email");
            String status = crs.getString("Status");
            String contact = crs.getString("ContactInfo");
            Date dateOfBirth = crs.getDate("DateofBirth");
            double fare = crs.getDouble("Fare");
            int deleted = crs.getInt("deleted");

            return new Student(id,
                    firstName,
                    middleName,
                    lastName,
                    nameExtension,
                    email,
                    status,
                    contact,
                    dateOfBirth,
                    fare,
                    clusterID,
                    yearID,
                    deleted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static DBParam[] paramList(Student student) {
        List<DBParam> paramList = new LinkedList<>();

        paramList.add(new DBParam(DBType.NUMERIC, "StudentID", student.getStudentID()));
        paramList.add(new DBParam(DBType.TEXT, "FirstName", student.getFirstName()));
        paramList.add(new DBParam(DBType.TEXT, "MiddleName", student.getMiddleName()));
        paramList.add(new DBParam(DBType.TEXT, "LastName", student.getLastName()));
        paramList.add(new DBParam(DBType.TEXT, "NameExtension", student.getNameExtension()));
        paramList.add(new DBParam(DBType.TEXT, "Email", student.getEmail()));
        paramList.add(new DBParam(DBType.TEXT, "Status", student.getStatus()));
        paramList.add(new DBParam(DBType.TEXT, "ContactInfo", student.getContact()));
        paramList.add(new DBParam(DBType.DATE, "DateofBirth", student.getDateOfBirth()));
        paramList.add(new DBParam(DBType.DECIMAL, "Fare", student.getFare()));
        int clusterID = student.clusterIDProperty().getValue().getClusterID();
        paramList.add(new DBParam(DBType.NUMERIC, "ClusterID", clusterID));
        paramList.add(new DBParam(DBType.NUMERIC, "yearID", student.getYearID().getYearID()));
        paramList.add(new DBParam(DBType.NUMERIC, "deleted", student.isDeleted()));

        return paramList.toArray(new DBParam[0]);
    }

    public static List<Student> getStudentList() {
        CachedRowSet crs = DB.select(TABLE);
        List<Student> list = new LinkedList<>();
        try {
            while (crs.next()) {
                Student student = data(crs);
                if (student != null) {
                    list.add(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void insert(Student student) {
        DB.insert(TABLE, paramList(student));
    }

    public static void delete(Student student) {
        DB.delete(TABLE, new DBParam(DBType.NUMERIC, "StudentID", student.getStudentID()));
    }

    public static void update(Student student) {

        DBParam[] params = paramList(student);
        DB.update(TABLE, new DBParam(DBType.NUMERIC, "StudentID",
                student.getStudentID()), params);

    }
}
