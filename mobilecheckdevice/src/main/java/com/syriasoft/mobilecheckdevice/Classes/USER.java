package com.syriasoft.mobilecheckdevice.Classes;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.LoginCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class USER {

    private final String loginUrl = "https://ratco-solutions.com/RatcoManagementSystem/appLoginEmployees.php";

    public int id;
    public int JobNumber;
    public String User;
    public String FirstName;
    public String LastName;
    public String Department;
    public String JobTitle;
    public int DirectManager;
    public int DepartmentManager;
    public double WorkLocationLa;
    public double WorkLocationLo;
    public String Mobile;
    public String Email;
    public String Pic;
    public String IDNumber;
    public String IDExpireDate;
    public String BirthDate;
    public String Nationality;
    public String PassportNumber;
    public String PassportExpireDate;
    public String ContractNumber;
    public String ContractStartDate;
    public int ContractDuration;
    public String ContractExpireDate;
    public String InsuranceExpireDate;
    public String Bank;
    public String BankAccount;
    public String BankIban;
    public int IDsWarningNotifications;
    public int PASSPORTsWarningNotification;
    public int CONTRACTsWarningNotification;
    public double Salary;
    public double VacationDays;
    public int VacationStatus;
    public int VacationAlternative;
    public int SickDays;
    public int EmergencyDays;
    public String JoinDate;
    public String Token;
    public int Language;
    public String AttendanceTime;
    public String LeaveTime;
    public String MyToken;


    public USER(int id, int jobNumber, String user, String firstName, String lastName, String department, String jobTitle, int directManager, int departmentManager, double workLocationLa, double workLocationLo, String mobile, String email, String pic, String IDNumber, String IDExpireDate, String birthDate, String nationality, String passportNumber, String passportExpireDate, String contractNumber, String contractStartDate, int contractDuration, String contractExpireDate, String insurance, String bank, String bankAccount, String bankIban, int idsWarning, int passWarning, int contractWarning, double salary, double vacationDays, int sickDays, int emergencyDays, int vacationStatus, int vacationAlternative, String joinDate, String token) {
        this.id = id;
        JobNumber = jobNumber;
        User = user;
        FirstName = firstName;
        LastName = lastName;
        Department = department;
        JobTitle = jobTitle;
        DirectManager = directManager;
        DepartmentManager = departmentManager;
        WorkLocationLa = workLocationLa;
        WorkLocationLo = workLocationLo;
        Mobile = mobile;
        Email = email;
        Pic = pic;
        this.IDNumber = IDNumber;
        this.IDExpireDate = IDExpireDate;
        BirthDate = birthDate;
        Nationality = nationality;
        PassportNumber = passportNumber;
        PassportExpireDate = passportExpireDate;
        ContractNumber = contractNumber;
        ContractStartDate = contractStartDate;
        ContractDuration = contractDuration;
        ContractExpireDate = contractExpireDate;
        InsuranceExpireDate = insurance;
        Bank = bank;
        BankAccount = bankAccount;
        BankIban = bankIban;
        IDsWarningNotifications = idsWarning;
        PASSPORTsWarningNotification = passWarning;
        CONTRACTsWarningNotification = contractWarning;
        Salary = salary;
        VacationDays = vacationDays;
        SickDays = sickDays;
        EmergencyDays = emergencyDays;
        VacationStatus = vacationStatus;
        VacationAlternative = vacationAlternative;
        JoinDate = joinDate;
        Token = token;
    }

    public USER(JSONObject jsonUser) throws JSONException {
        if (jsonUser != null) {
            this.id = jsonUser.getInt("id");
            this.JobNumber = jsonUser.getInt("JobNumber");
            this.User = jsonUser.getString("User");
            this.FirstName = jsonUser.getString("FirstName");
            this.LastName = jsonUser.getString("LastName");
            this.Department = jsonUser.getString("Department");
            this.JobTitle = jsonUser.getString("JobTitle");
            this.DirectManager = jsonUser.getInt("DirectManager");
            this.DepartmentManager = jsonUser.getInt("DepartmentManager");
            this.WorkLocationLa = jsonUser.getInt("WorkLocationLa");
            this.WorkLocationLo = jsonUser.getInt("WorkLocationLo");
            this.Mobile = jsonUser.getString("Mobile");
            this.Email = jsonUser.getString("Email");
            this.Pic = jsonUser.getString("Pic");
            this.IDNumber = jsonUser.getString("IDNumber");
            this.IDExpireDate = jsonUser.getString("IDExpireDate");
            this.BirthDate = jsonUser.getString("BirthDate");
            this.Nationality = jsonUser.getString("Nationality");
            this.PassportNumber = jsonUser.getString("PassportNumber");
            this.PassportExpireDate = jsonUser.getString("PassportExpireDate");
            this.ContractNumber = jsonUser.getString("ContractNumber");
            this.ContractStartDate = jsonUser.getString("ContractStartDate");
            this.ContractDuration = jsonUser.getInt("ContractDuration");
            this.ContractExpireDate = jsonUser.getString("ContractExpireDate");
            this.InsuranceExpireDate = jsonUser.getString("InsuranceExpireDate");
            this.Bank = jsonUser.getString("Bank");
            this.BankAccount = jsonUser.getString("BankAccount");
            this.BankIban = jsonUser.getString("BankIban");
            this.IDsWarningNotifications = jsonUser.getInt("IDsWarningNotifications");
            this.PASSPORTsWarningNotification = jsonUser.getInt("PASSPORTsWarningNotification");
            this.CONTRACTsWarningNotification = jsonUser.getInt("CONTRACTsWarningNotification");
            this.Salary = jsonUser.getDouble("Salary");
            this.VacationDays = jsonUser.getInt("VacationDays");
            this.VacationStatus = jsonUser.getInt("VacationStatus");
            this.VacationAlternative = jsonUser.getInt("VacationAlternative");
            this.SickDays = jsonUser.getInt("SickDays");
            this.EmergencyDays = jsonUser.getInt("EmergencyDays");
            this.JoinDate = jsonUser.getString("JoinDate");
            this.Token = jsonUser.getString("Token");
            this.Language = jsonUser.getInt("Language");
            this.AttendanceTime = jsonUser.getString("AttendanceTime");
            this.LeaveTime = jsonUser.getString("LeaveTime");
            //this.MyToken = jsonUser.getString("MyToken");
        }
        else {
            throw new NullPointerException();
        }
    }

    public USER() {

    }

    public void login(String jobNumber, String password, RequestQueue queue, LoginCallback loginCallback) {
        queue.add(new StringRequest(Request.Method.POST, loginUrl, response -> {
            Log.d("loginActivity", "login response: "+response);
            if (response.equals("0")) {
                loginCallback.onUserOrPasswordMistake();
            }
            else {
                try {
                    JSONArray arr = new JSONArray(response);
                    JSONObject json = arr.getJSONObject(0);
                    USER user = new USER(json);
                    loginCallback.loggedIn(user);
                } catch (JSONException e) {
                    loginCallback.onError(e.getMessage());
                }
            }

        }, error -> {
            loginCallback.onError(error.toString());
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String ,String> pars = new HashMap<>();
                pars.put("user" , jobNumber);
                pars.put("password" , password);
                return pars;
            }
        });
    }
}
