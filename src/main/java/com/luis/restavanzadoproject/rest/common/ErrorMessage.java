package com.luis.restavanzadoproject.rest.common;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorMessage {

    private String description;
    private LocalDateTime date;
}
