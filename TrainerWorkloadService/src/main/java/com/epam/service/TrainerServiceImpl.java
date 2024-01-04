package com.epam.service;

import com.epam.model.Trainer;
import com.epam.model.dto.ActionType;
import com.epam.model.dto.TrainerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final AuthenticationService authenticationService;

    Map<String, Trainer> sum = new HashMap<>();

    @Override
    public void save(TrainerDto trainerDto, String token) {

        if (!authenticationService.verifyToken(token)) {
            throw new RuntimeException("Wrong token");
        }

        if (trainerDto.getActionType().equals(ActionType.DELETE)) {
            trainerDto.setDuration(-trainerDto.getDuration());
        }

        Trainer trainer;
        Integer year = trainerDto.getDate().getYear();
        Integer month = trainerDto.getDate().getMonthValue();

        if (sum.get(trainerDto.getUsername()) != null) {
            trainer = sum.get(trainerDto.getUsername());

            updateDuration(trainerDto, trainer, year, month);
        } else {
            trainer = createTrainer(trainerDto, month, year);
        }

        sum.put(trainerDto.getUsername(), trainer);
    }

    private static Trainer createTrainer(TrainerDto trainerDto, Integer month, Integer year) {
        Trainer trainer;
        Map<Integer, Long> months = new HashMap<>();
        months.put(month, trainerDto.getDuration());
        Map<Integer, Map<Integer, Long>> years = new HashMap<>();
        years.put(year, months);

        trainer = Trainer.builder()
                         .username(trainerDto.getUsername())
                         .firstName(trainerDto.getFirstName())
                         .lastName(trainerDto.getLastName())
                         .isActive(trainerDto.getIsActive())
                         .duration(years)
                         .build();
        return trainer;
    }

    private static void updateDuration(TrainerDto trainerDto, Trainer trainer, Integer year, Integer month) {
        trainer.getDuration()
               .computeIfAbsent(year, k -> new HashMap<>())
               .merge(month, trainerDto.getDuration(), Long::sum);
    }
}