package com.example.testfrontendbackenddb.service;

import com.example.testfrontendbackenddb.entity.RegisterStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.List;

@Service
public class RegisterStudentService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Insert student with photo support
    public void insertStudent(RegisterStudent student) {
        String sql = "INSERT INTO register_student (" +
                "full_name, dob, age, gender, religion, category, nationality, mobile_no, email_id, aadhar_no, " +
                "course, semester_year, institute_name, course_name, branch, year_of_study, date_of_admission, hostel_join_date, " +
                "photo_blob, father_name, father_occupation, father_education, father_email, father_mobile, " +
                "mother_name, mother_occupation, mother_education, mother_email, mother_mobile, " +
                "permanent_address, city_district, state, pin_code, phone_residence, phone_office, office_address, " +
                "local_guardian_name, local_guardian_address, local_guardian_phone, local_guardian_mobile, " +
                "emergency_contact_name, emergency_contact_no, blood_group, serious_disease, regular_medication, " +
                "hospital_record, emergency_medicine, allergic_to, student_password) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Date sqlDob = student.getDob() != null ? new Date(student.getDob().getTime()) : null;
        Date sqlDoa = student.getDateOfAdmission() != null ? new Date(student.getDateOfAdmission().getTime()) : null;
        Date sqlHostelJoin = student.getHostelJoinDate() != null ? new Date(student.getHostelJoinDate().getTime()) : null;

        jdbcTemplate.update(sql,
                student.getFullName(),
                sqlDob,
                student.getAge(),
                student.getGender(),
                student.getReligion(),
                student.getCategory(),
                student.getNationality(),
                student.getMobileNo(),
                student.getEmailId(),
                student.getAadharNo(),
                student.getCourse(),
                student.getSemesterYear(),
                student.getInstituteName(),
                student.getCourseName(),
                student.getBranch(),
                student.getYearOfStudy(),
                sqlDoa,
                sqlHostelJoin,
                student.getPhotoBlob(),
                student.getFatherName(),
                student.getFatherOccupation(),
                student.getFatherEducation(),
                student.getFatherEmail(),
                student.getFatherMobile(),
                student.getMotherName(),
                student.getMotherOccupation(),
                student.getMotherEducation(),
                student.getMotherEmail(),
                student.getMotherMobile(),
                student.getPermanentAddress(),
                student.getCityDistrict(),
                student.getState(),
                student.getPinCode(),
                student.getPhoneResidence(),
                student.getPhoneOffice(),
                student.getOfficeAddress(),
                student.getLocalGuardianName(),
                student.getLocalGuardianAddress(),
                student.getLocalGuardianPhone(),
                student.getLocalGuardianMobile(),
                student.getEmergencyContactName(),
                student.getEmergencyContactNo(),
                student.getBloodGroup(),
                student.getSeriousDisease(),
                student.getRegularMedication(),
                student.getHospitalRecord(),
                student.getEmergencyMedicine(),
                student.getAllergicTo(),
                student.getStudentPassword()
        );
    }

    // Get student by ID
    public RegisterStudent getStudentById(Integer id) {
        String sql = "SELECT * FROM register_student WHERE id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            RegisterStudent s = new RegisterStudent();
            s.setId(rs.getInt("id"));
            s.setFullName(rs.getString("full_name"));
            s.setDob(rs.getDate("dob"));
            s.setAge(rs.getInt("age"));
            s.setGender(rs.getString("gender"));
            s.setReligion(rs.getString("religion"));
            s.setCategory(rs.getString("category"));
            s.setNationality(rs.getString("nationality"));
            s.setMobileNo(rs.getString("mobile_no"));
            s.setEmailId(rs.getString("email_id"));
            s.setAadharNo(rs.getString("aadhar_no"));
            s.setCourse(rs.getString("course"));
            s.setSemesterYear(rs.getString("semester_year"));
            s.setInstituteName(rs.getString("institute_name"));
            s.setCourseName(rs.getString("course_name"));
            s.setBranch(rs.getString("branch"));
            s.setYearOfStudy(rs.getString("year_of_study"));
            s.setDateOfAdmission(rs.getDate("date_of_admission"));
            s.setHostelJoinDate(rs.getDate("hostel_join_date"));
            s.setPhotoBlob(rs.getBytes("photo_blob"));
            s.setFatherName(rs.getString("father_name"));
            s.setFatherOccupation(rs.getString("father_occupation"));
            s.setFatherEducation(rs.getString("father_education"));
            s.setFatherEmail(rs.getString("father_email"));
            s.setFatherMobile(rs.getString("father_mobile"));
            s.setMotherName(rs.getString("mother_name"));
            s.setMotherOccupation(rs.getString("mother_occupation"));
            s.setMotherEducation(rs.getString("mother_education"));
            s.setMotherEmail(rs.getString("mother_email"));
            s.setMotherMobile(rs.getString("mother_mobile"));
            s.setPermanentAddress(rs.getString("permanent_address"));
            s.setCityDistrict(rs.getString("city_district"));
            s.setState(rs.getString("state"));
            s.setPinCode(rs.getString("pin_code"));
            s.setPhoneResidence(rs.getString("phone_residence"));
            s.setPhoneOffice(rs.getString("phone_office"));
            s.setOfficeAddress(rs.getString("office_address"));
            s.setLocalGuardianName(rs.getString("local_guardian_name"));
            s.setLocalGuardianAddress(rs.getString("local_guardian_address"));
            s.setLocalGuardianPhone(rs.getString("local_guardian_phone"));
            s.setLocalGuardianMobile(rs.getString("local_guardian_mobile"));
            s.setEmergencyContactName(rs.getString("emergency_contact_name"));
            s.setEmergencyContactNo(rs.getString("emergency_contact_no"));
            s.setBloodGroup(rs.getString("blood_group"));
            s.setSeriousDisease(rs.getString("serious_disease"));
            s.setRegularMedication(rs.getString("regular_medication"));
            s.setHospitalRecord(rs.getString("hospital_record"));
            s.setEmergencyMedicine(rs.getString("emergency_medicine"));
            s.setAllergicTo(rs.getString("allergic_to"));
            s.setStudentPassword(rs.getString("student_password"));
            return s;
        });
    }

    // Get all students
    public List<RegisterStudent> getAllStudents() {
        String sql = "SELECT * FROM register_student";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RegisterStudent s = new RegisterStudent();
            s.setId(rs.getInt("id"));
            s.setFullName(rs.getString("full_name"));
            s.setDob(rs.getDate("dob"));
            s.setAge(rs.getInt("age"));
            s.setGender(rs.getString("gender"));
            s.setReligion(rs.getString("religion"));
            s.setCategory(rs.getString("category"));
            s.setNationality(rs.getString("nationality"));
            s.setMobileNo(rs.getString("mobile_no"));
            s.setEmailId(rs.getString("email_id"));
            s.setAadharNo(rs.getString("aadhar_no"));
            s.setCourse(rs.getString("course"));
            s.setSemesterYear(rs.getString("semester_year"));
            s.setInstituteName(rs.getString("institute_name"));
            s.setCourseName(rs.getString("course_name"));
            s.setBranch(rs.getString("branch"));
            s.setYearOfStudy(rs.getString("year_of_study"));
            s.setDateOfAdmission(rs.getDate("date_of_admission"));
            s.setHostelJoinDate(rs.getDate("hostel_join_date"));
            s.setPhotoBlob(rs.getBytes("photo_blob"));
            s.setFatherName(rs.getString("father_name"));
            s.setFatherOccupation(rs.getString("father_occupation"));
            s.setFatherEducation(rs.getString("father_education"));
            s.setFatherEmail(rs.getString("father_email"));
            s.setFatherMobile(rs.getString("father_mobile"));
            s.setMotherName(rs.getString("mother_name"));
            s.setMotherOccupation(rs.getString("mother_occupation"));
            s.setMotherEducation(rs.getString("mother_education"));
            s.setMotherEmail(rs.getString("mother_email"));
            s.setMotherMobile(rs.getString("mother_mobile"));
            s.setPermanentAddress(rs.getString("permanent_address"));
            s.setCityDistrict(rs.getString("city_district"));
            s.setState(rs.getString("state"));
            s.setPinCode(rs.getString("pin_code"));
            s.setPhoneResidence(rs.getString("phone_residence"));
            s.setPhoneOffice(rs.getString("phone_office"));
            s.setOfficeAddress(rs.getString("office_address"));
            s.setLocalGuardianName(rs.getString("local_guardian_name"));
            s.setLocalGuardianAddress(rs.getString("local_guardian_address"));
            s.setLocalGuardianPhone(rs.getString("local_guardian_phone"));
            s.setLocalGuardianMobile(rs.getString("local_guardian_mobile"));
            s.setEmergencyContactName(rs.getString("emergency_contact_name"));
            s.setEmergencyContactNo(rs.getString("emergency_contact_no"));
            s.setBloodGroup(rs.getString("blood_group"));
            s.setSeriousDisease(rs.getString("serious_disease"));
            s.setRegularMedication(rs.getString("regular_medication"));
            s.setHospitalRecord(rs.getString("hospital_record"));
            s.setEmergencyMedicine(rs.getString("emergency_medicine"));
            s.setAllergicTo(rs.getString("allergic_to"));
            s.setStudentPassword(rs.getString("student_password"));
            return s;
        });
    }

    // Update student with photo preservation
    public boolean updateStudent(Integer id, RegisterStudent student) {
        RegisterStudent existing = getStudentById(id);
        if (existing == null) return false;

        byte[] photoBlobToUse = student.getPhotoBlob() != null ? student.getPhotoBlob() : existing.getPhotoBlob();

        String sql = "UPDATE register_student SET " +
                "full_name=?, dob=?, age=?, gender=?, religion=?, category=?, nationality=?, " +
                "mobile_no=?, email_id=?, aadhar_no=?, course=?, semester_year=?, institute_name=?, course_name=?, " +
                "branch=?, year_of_study=?, date_of_admission=?, hostel_join_date=?, photo_blob=?, father_name=?, " +
                "father_occupation=?, father_education=?, father_email=?, father_mobile=?, mother_name=?, mother_occupation=?, " +
                "mother_education=?, mother_email=?, mother_mobile=?, permanent_address=?, city_district=?, state=?, pin_code=?, " +
                "phone_residence=?, phone_office=?, office_address=?, local_guardian_name=?, local_guardian_address=?, " +
                "local_guardian_phone=?, local_guardian_mobile=?, emergency_contact_name=?, emergency_contact_no=?, blood_group=?, " +
                "serious_disease=?, regular_medication=?, hospital_record=?, emergency_medicine=?, allergic_to=?, student_password=? " +
                "WHERE id=?";

        Date sqlDob = student.getDob() != null ? new Date(student.getDob().getTime()) : null;
        Date sqlDoa = student.getDateOfAdmission() != null ? new Date(student.getDateOfAdmission().getTime()) : null;
        Date sqlHostelJoin = student.getHostelJoinDate() != null ? new Date(student.getHostelJoinDate().getTime()) : null;

        int rows = jdbcTemplate.update(sql,
                student.getFullName(), sqlDob, student.getAge(), student.getGender(), student.getReligion(),
                student.getCategory(), student.getNationality(), student.getMobileNo(), student.getEmailId(), student.getAadharNo(),
                student.getCourse(), student.getSemesterYear(), student.getInstituteName(), student.getCourseName(), student.getBranch(),
                student.getYearOfStudy(), sqlDoa, sqlHostelJoin, photoBlobToUse,
                student.getFatherName(), student.getFatherOccupation(), student.getFatherEducation(), student.getFatherEmail(), student.getFatherMobile(),
                student.getMotherName(), student.getMotherOccupation(), student.getMotherEducation(), student.getMotherEmail(), student.getMotherMobile(),
                student.getPermanentAddress(), student.getCityDistrict(), student.getState(), student.getPinCode(), student.getPhoneResidence(), student.getPhoneOffice(), student.getOfficeAddress(),
                student.getLocalGuardianName(), student.getLocalGuardianAddress(), student.getLocalGuardianPhone(), student.getLocalGuardianMobile(),
                student.getEmergencyContactName(), student.getEmergencyContactNo(), student.getBloodGroup(), student.getSeriousDisease(), student.getRegularMedication(),
                student.getHospitalRecord(), student.getEmergencyMedicine(), student.getAllergicTo(), student.getStudentPassword(),
                id
        );
        return rows > 0;
    }

    // Delete student by ID
    public boolean deleteStudent(Integer id) {
        return jdbcTemplate.update("DELETE FROM register_student WHERE id=?", id) > 0;
    }
}
