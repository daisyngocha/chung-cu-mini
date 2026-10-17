package vn.chungcumini.backend.suco;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TiepNhanRequest(@NotNull LocalDateTime lichHen) {
}
