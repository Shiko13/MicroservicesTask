package com.epam.service;


import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import com.epam.model.dto.TrainerShortDtoInput;

import java.util.List;

public interface TraineeService {

    TraineeSaveDtoOutput save(TraineeDtoInput traineeDtoInput);

    TraineeDtoOutput getByUsername(String username);

    TraineeUpdateDtoOutput updateProfile(String userName, TraineeProfileDtoInput traineeDtoInput);

    TraineeUpdateListDtoOutput updateTrainerList(String username,
                                                 List<TrainerShortDtoInput> trainersUsernames);

    void deleteByUsername(String username);
}
