package com.sillador.strecs.services.impl;

import com.sillador.strecs.dto.StudentDTO;
import com.sillador.strecs.entity.Room;
import com.sillador.strecs.entity.Student;
import com.sillador.strecs.repositories.RoomRepository;
import com.sillador.strecs.repositories.StudentRepository;
import com.sillador.strecs.repositories.specifications.StudentSpecification;
import com.sillador.strecs.services.RoomService;
import com.sillador.strecs.services.StudentService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }


    @Override
    public BaseResponse getAll(@org.jetbrains.annotations.NotNull Map<String, String> query) {
        List<Room> rooms = roomRepository.findAll();
        BaseResponse baseResponse = new BaseResponse().build(rooms);
        baseResponse.setPage(new com.sillador.strecs.utility.Page(rooms.size(), rooms.size()));
        return baseResponse;
    }

    @Override
    public Optional<Room> findById(long id) {
        return roomRepository.findById(id);
    }
}
