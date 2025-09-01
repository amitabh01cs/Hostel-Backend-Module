package com.example.testfrontendbackenddb.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

@Entity
@Table(name = "register_student")
public class RegisterStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "religion")
    private String religion;

    @Column(name = "category")
    private String category;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "aadhar_no")
    private String aadharNo;

    @Column(name = "course")
    private String course;

    @Column(name = "semester_year")
    private String semesterYear;

    @Column(name = "institute_name")
    private String instituteName;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "year_of_study")
    private String yearOfStudy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_admission")
    private Date dateOfAdmission;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "hostel_join_date")
    private Date hostelJoinDate;

    @Column(name = "photo_path")
    private String photoPath;

    @Lob
    @Column(name = "photo_blob", columnDefinition = "LONGBLOB") // for MySQL
    private byte[] photoBlob;


    @Column(name = "father_name")
    private String fatherName;
    @Column(name = "father_occupation")
    private String fatherOccupation;
    @Column(name = "father_education")
    private String fatherEducation;
    @Column(name = "father_email")
    private String fatherEmail;
    @Column(name = "father_mobile")
    private String fatherMobile;

    @Column(name = "mother_name")
    private String motherName;
    @Column(name = "mother_occupation")
    private String motherOccupation;
    @Column(name = "mother_education")
    private String motherEducation;
    @Column(name = "mother_email")
    private String motherEmail;
    @Column(name = "mother_mobile")
    private String motherMobile;

    @Column(name = "permanent_address", columnDefinition = "TEXT")
    private String permanentAddress;
    @Column(name = "city_district")
    private String cityDistrict;
    @Column(name = "state")
    private String state;
    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "phone_residence")
    private String phoneResidence;
    @Column(name = "phone_office")
    private String phoneOffice;

    @Column(name = "office_address", columnDefinition = "TEXT")
    private String officeAddress;

    @Column(name = "local_guardian_name")
    private String localGuardianName;
    @Column(name = "local_guardian_address", columnDefinition = "TEXT")
    private String localGuardianAddress;
    @Column(name = "local_guardian_phone")
    private String localGuardianPhone;
    @Column(name = "local_guardian_mobile")
    private String localGuardianMobile;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    @Column(name = "emergency_contact_no")
    private String emergencyContactNo;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "serious_disease", columnDefinition = "TEXT")
    private String seriousDisease;
    @Column(name = "regular_medication", columnDefinition = "TEXT")
    private String regularMedication;
    @Column(name = "hospital_record", columnDefinition = "TEXT")
    private String hospitalRecord;
    @Column(name = "emergency_medicine", columnDefinition = "TEXT")
    private String emergencyMedicine;
    @Column(name = "allergic_to", columnDefinition = "TEXT")
    private String allergicTo;

    @Column(name = "student_password", columnDefinition = "TEXT")
    private String studentPassword;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getSemesterYear() { return semesterYear; }
    public void setSemesterYear(String semesterYear) { this.semesterYear = semesterYear; }
    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public Date getDateOfAdmission() { return dateOfAdmission; }
    public void setDateOfAdmission(Date dateOfAdmission) { this.dateOfAdmission = dateOfAdmission; }
    public Date getHostelJoinDate() { return hostelJoinDate; }
    public void setHostelJoinDate(Date hostelJoinDate) { this.hostelJoinDate = hostelJoinDate; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public byte[] getPhotoBlob() { return photoBlob; }
    public void setPhotoBlob(byte[] photoBlob) { this.photoBlob = photoBlob; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public String getFatherOccupation() { return fatherOccupation; }
    public void setFatherOccupation(String fatherOccupation) { this.fatherOccupation = fatherOccupation; }
    public String getFatherEducation() { return fatherEducation; }
    public void setFatherEducation(String fatherEducation) { this.fatherEducation = fatherEducation; }
    public String getFatherEmail() { return fatherEmail; }
    public void setFatherEmail(String fatherEmail) { this.fatherEmail = fatherEmail; }
    public String getFatherMobile() { return fatherMobile; }
    public void setFatherMobile(String fatherMobile) { this.fatherMobile = fatherMobile; }
    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }
    public String getMotherOccupation() { return motherOccupation; }
    public void setMotherOccupation(String motherOccupation) { this.motherOccupation = motherOccupation; }
    public String getMotherEducation() { return motherEducation; }
    public void setMotherEducation(String motherEducation) { this.motherEducation = motherEducation; }
    public String getMotherEmail() { return motherEmail; }
    public void setMotherEmail(String motherEmail) { this.motherEmail = motherEmail; }
    public String getMotherMobile() { return motherMobile; }
    public void setMotherMobile(String motherMobile) { this.motherMobile = motherMobile; }
    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }
    public String getCityDistrict() { return cityDistrict; }
    public void setCityDistrict(String cityDistrict) { this.cityDistrict = cityDistrict; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    public String getPhoneResidence() { return phoneResidence; }
    public void setPhoneResidence(String phoneResidence) { this.phoneResidence = phoneResidence; }
    public String getPhoneOffice() { return phoneOffice; }
    public void setPhoneOffice(String phoneOffice) { this.phoneOffice = phoneOffice; }
    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }
    public String getLocalGuardianName() { return localGuardianName; }
    public void setLocalGuardianName(String localGuardianName) { this.localGuardianName = localGuardianName; }
    public String getLocalGuardianAddress() { return localGuardianAddress; }
    public void setLocalGuardianAddress(String localGuardianAddress) { this.localGuardianAddress = localGuardianAddress; }
    public String getLocalGuardianPhone() { return localGuardianPhone; }
    public void setLocalGuardianPhone(String localGuardianPhone) { this.localGuardianPhone = localGuardianPhone; }
    public String getLocalGuardianMobile() { return localGuardianMobile; }
    public void setLocalGuardianMobile(String localGuardianMobile) { this.localGuardianMobile = localGuardianMobile; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactNo() { return emergencyContactNo; }
    public void setEmergencyContactNo(String emergencyContactNo) { this.emergencyContactNo = emergencyContactNo; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getSeriousDisease() { return seriousDisease; }
    public void setSeriousDisease(String seriousDisease) { this.seriousDisease = seriousDisease; }
    public String getRegularMedication() { return regularMedication; }
    public void setRegularMedication(String regularMedication) { this.regularMedication = regularMedication; }
    public String getHospitalRecord() { return hospitalRecord; }
    public void setHospitalRecord(String hospitalRecord) { this.hospitalRecord = hospitalRecord; }
    public String getEmergencyMedicine() { return emergencyMedicine; }
    public void setEmergencyMedicine(String emergencyMedicine) { this.emergencyMedicine = emergencyMedicine; }
    public String getAllergicTo() { return allergicTo; }
    public void setAllergicTo(String allergicTo) { this.allergicTo = allergicTo; }
    public String getStudentPassword() { return studentPassword; }
    public void setStudentPassword(String studentPassword) { this.studentPassword = studentPassword; }
}
