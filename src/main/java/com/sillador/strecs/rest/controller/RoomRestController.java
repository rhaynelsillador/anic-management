package com.sillador.strecs.rest.controller;

import com.sillador.strecs.services.RoomService;
import com.sillador.strecs.services.YearLevelService;
import com.sillador.strecs.utility.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/room")
public class RoomRestController {

    private final RoomService roomService;

    public RoomRestController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping
    public BaseResponse getAll(@RequestParam(required = false) Map<String, String> query){
        return roomService.getAll(query);
    }


}
