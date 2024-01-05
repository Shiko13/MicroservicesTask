package com.epam.service;

import com.epam.model.Training;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TrainingService {

    Training save(TrainingDtoInput trainingDtoInput, HttpServletRequest request);

    List<TrainingForTraineeDtoOutput> findByDateRangeAndTraineeUsername(TrainingTraineeSpecification specification);

    List<TrainingForTrainerDtoOutput> findByDateRangeAndTrainerUsername(
                                                                        TrainingTrainerSpecification specification);

    void deleteById(Long id, HttpServletRequest request);
}
