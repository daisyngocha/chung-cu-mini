package vn.chungcumini.backend.baothanhtoan;

import jakarta.validation.constraints.NotBlank;

public record TuChoiRequest(@NotBlank String lyDo) {
}
