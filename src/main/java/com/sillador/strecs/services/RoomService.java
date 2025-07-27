package com.sillador.strecs.services;

import com.sillador.strecs.entity.Room;
import com.sillador.strecs.utility.BaseResponse;

import java.util.Map;
import java.util.Optional;

public interface RoomService {

    BaseResponse getAll(Map<String, String> query);

    Optional<Room> findById(long id);
}
