package vn.chungcumini.backend.toanha;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.chungcumini.backend.domain.ToaNha;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@RestController
@RequestMapping("/api/toa-nha")
public class ToaNhaController {

    private final ToaNhaRepository toaNhaRepository;

    public ToaNhaController(ToaNhaRepository toaNhaRepository) {
        this.toaNhaRepository = toaNhaRepository;
    }

    public record ToaNhaResponse(String maToa, String tenToa) {
    }

    @GetMapping("/mac-dinh")
    public ToaNhaResponse macDinh() {
        ToaNha toaNha = toaNhaRepository.findById("TOA01").orElseThrow();
        return new ToaNhaResponse(toaNha.getMaToa(), toaNha.getTenToa());
    }
}
