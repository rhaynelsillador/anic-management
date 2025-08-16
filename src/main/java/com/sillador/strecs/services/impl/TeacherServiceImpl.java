package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.TeacherDTO;
import com.sillador.strecs.entity.Teacher;
import com.sillador.strecs.repositories.TeacherRepository;
import com.sillador.strecs.repositories.specifications.BaseSpecification;
import com.sillador.strecs.repositories.specifications.TeacherSpecification;
import com.sillador.strecs.services.TeacherService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends BaseService implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Override
    public BaseResponse getAllTeachers(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        String sorting = query.getOrDefault("sort", "id,desc");
        int limit = Integer.parseInt(query.getOrDefault("limit", String.valueOf(10)));
        int page = Integer.parseInt(query.getOrDefault("page", String.valueOf(0)));

        Sort sort = BaseSpecification.sorting(sorting, TeacherSpecification.getSortedKeys());
        Specification<Teacher> spec = TeacherSpecification.filter(query);

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Teacher> studentPage = teacherRepository.findAll(spec, pageable);

        List<TeacherDTO> teacherDTOS = new ArrayList<>();
        studentPage.getContent().forEach(d -> teacherDTOS.add(toDTO(d)));

        BaseResponse baseResponse = new BaseResponse().build(teacherDTOS);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(studentPage.getTotalElements(), studentPage.getSize()));
        return baseResponse;
    }

    @Override
    public Optional<Teacher> findById(long id) {
        return teacherRepository.findById(id);
    }

    @Override
    public Optional<Teacher> findByAccountRef(long accountRef) {
        // Since Account.accountRef references Teacher.id, we can use findById
        return teacherRepository.findById(accountRef);
    }

    @Override
    public BaseResponse createNewTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        toTeacher(teacherDTO, teacher);
        teacherRepository.save(teacher);
        return success();
    }

    @Override
    public BaseResponse updateTeacher(long id, TeacherDTO teacherDTO) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(id);
        if(optionalTeacher.isEmpty()){
            return error("Teacher does not exist");
        }

        Teacher teacher = toTeacher(teacherDTO, optionalTeacher.get());
        teacherRepository.save(teacher);
        return success();
    }

    public TeacherDTO toDTO(Teacher d) {
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(d.getId());
        teacherDTO.setContactNo(d.getContactNo());
        teacherDTO.setEmployeeNo(d.getEmployeeNo());
        teacherDTO.setPosition(d.getPosition());
        teacherDTO.setEmail(d.getEmail());

        teacherDTO.setFirstName(d.getFirstName());
        teacherDTO.setLastName(d.getLastName());
        teacherDTO.setPhotoUrl(d.getPhotoUrl());
        teacherDTO.setFullName(d.getFullName());
        return teacherDTO;
    }

    private Teacher toTeacher(TeacherDTO dto, Teacher teacher){
        if(teacher == null){
            teacher = new Teacher();
        }
        teacher.setEmployeeNo(dto.getEmployeeNo());
        teacher.setEmail(dto.getEmail());
        teacher.setPosition(dto.getPosition());
        teacher.setFirstName(dto.getFirstName());
        teacher.setLastName(dto.getLastName());
        teacher.setContactNo(dto.getContactNo());
        return teacher;
    }
}
