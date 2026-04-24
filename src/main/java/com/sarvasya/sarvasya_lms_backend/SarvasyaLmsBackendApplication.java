package com.sarvasya.sarvasya_lms_backend;

import com.sarvasya.sarvasya_lms_backend.api.*;
import com.sarvasya.sarvasya_lms_backend.config.*;
import com.sarvasya.sarvasya_lms_backend.controller.admitcard.*;
import com.sarvasya.sarvasya_lms_backend.controller.assignment.*;
import com.sarvasya.sarvasya_lms_backend.controller.attendance.*;
import com.sarvasya.sarvasya_lms_backend.controller.auth.*;
import com.sarvasya.sarvasya_lms_backend.controller.bus.*;
import com.sarvasya.sarvasya_lms_backend.controller.calendar.*;
import com.sarvasya.sarvasya_lms_backend.controller.classes.*;
import com.sarvasya.sarvasya_lms_backend.controller.course.*;
import com.sarvasya.sarvasya_lms_backend.controller.degree.*;
import com.sarvasya.sarvasya_lms_backend.controller.department.*;
import com.sarvasya.sarvasya_lms_backend.controller.exam.*;
import com.sarvasya.sarvasya_lms_backend.controller.fee.*;
import com.sarvasya.sarvasya_lms_backend.controller.professor.*;
import com.sarvasya.sarvasya_lms_backend.controller.result.*;
import com.sarvasya.sarvasya_lms_backend.controller.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.controller.tenant.*;
import com.sarvasya.sarvasya_lms_backend.controller.timetable.*;
import com.sarvasya.sarvasya_lms_backend.controller.user.*;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.*;
import com.sarvasya.sarvasya_lms_backend.dto.auth.*;
import com.sarvasya.sarvasya_lms_backend.dto.bus.*;
import com.sarvasya.sarvasya_lms_backend.dto.common.*;
import com.sarvasya.sarvasya_lms_backend.dto.fee.*;
import com.sarvasya.sarvasya_lms_backend.dto.professor.*;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt.*;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.upload.*;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.*;
import com.sarvasya.sarvasya_lms_backend.dto.timetable.*;
import com.sarvasya.sarvasya_lms_backend.dto.user.*;
import com.sarvasya.sarvasya_lms_backend.model.attendance.*;
import com.sarvasya.sarvasya_lms_backend.model.calendar.*;
import com.sarvasya.sarvasya_lms_backend.model.common.*;
import com.sarvasya.sarvasya_lms_backend.model.fee.*;
import com.sarvasya.sarvasya_lms_backend.model.professor.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.model.tenant.*;
import com.sarvasya.sarvasya_lms_backend.repository.admitcard.*;
import com.sarvasya.sarvasya_lms_backend.repository.assignment.*;
import com.sarvasya.sarvasya_lms_backend.repository.attendance.*;
import com.sarvasya.sarvasya_lms_backend.repository.bus.*;
import com.sarvasya.sarvasya_lms_backend.repository.calendar.*;
import com.sarvasya.sarvasya_lms_backend.repository.classes.*;
import com.sarvasya.sarvasya_lms_backend.repository.course.*;
import com.sarvasya.sarvasya_lms_backend.repository.degree.*;
import com.sarvasya.sarvasya_lms_backend.repository.department.*;
import com.sarvasya.sarvasya_lms_backend.repository.exam.*;
import com.sarvasya.sarvasya_lms_backend.repository.fee.*;
import com.sarvasya.sarvasya_lms_backend.repository.professor.*;
import com.sarvasya.sarvasya_lms_backend.repository.result.*;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.repository.tenant.*;
import com.sarvasya.sarvasya_lms_backend.repository.timetable.*;
import com.sarvasya.sarvasya_lms_backend.repository.user.*;
import com.sarvasya.sarvasya_lms_backend.security.*;
import com.sarvasya.sarvasya_lms_backend.service.admitcard.*;
import com.sarvasya.sarvasya_lms_backend.service.assignment.*;
import com.sarvasya.sarvasya_lms_backend.service.attendance.*;
import com.sarvasya.sarvasya_lms_backend.service.auth.*;
import com.sarvasya.sarvasya_lms_backend.service.bus.*;
import com.sarvasya.sarvasya_lms_backend.service.calendar.*;
import com.sarvasya.sarvasya_lms_backend.service.classes.*;
import com.sarvasya.sarvasya_lms_backend.service.common.*;
import com.sarvasya.sarvasya_lms_backend.service.course.*;
import com.sarvasya.sarvasya_lms_backend.service.degree.*;
import com.sarvasya.sarvasya_lms_backend.service.department.*;
import com.sarvasya.sarvasya_lms_backend.service.exam.*;
import com.sarvasya.sarvasya_lms_backend.service.fee.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.admitcard.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.assignment.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.attendance.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.auth.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.bus.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.calendar.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.classes.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.course.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.degree.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.department.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.exam.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.fee.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.professor.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.result.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.tenant.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.timetable.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.upload.*;
import com.sarvasya.sarvasya_lms_backend.service.impl.user.*;
import com.sarvasya.sarvasya_lms_backend.service.professor.*;
import com.sarvasya.sarvasya_lms_backend.service.result.*;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.*;
import com.sarvasya.sarvasya_lms_backend.service.tenant.*;
import com.sarvasya.sarvasya_lms_backend.service.timetable.*;
import com.sarvasya.sarvasya_lms_backend.service.upload.*;
import com.sarvasya.sarvasya_lms_backend.service.user.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class SarvasyaLmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SarvasyaLmsBackendApplication.class, args);
	}

}









