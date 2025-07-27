package com.sillador.strecs.utility;

import lombok.Getter;

import java.time.Year;

@Getter
public class Config {

    private final int schoolYear = Year.now().getValue();

}
