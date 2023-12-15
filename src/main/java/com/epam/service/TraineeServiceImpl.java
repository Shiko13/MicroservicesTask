package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.error.NotFoundException;
import com.epam.mapper.TraineeMapper;
import com.epam.mapper.UserMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.User;
import com.epam.model.dto.TraineeDtoInput;
import com.epam.model.dto.TraineeDtoOutput;
import com.epam.model.dto.TraineeProfileDtoInput;
import com.epam.model.dto.TraineeSaveDtoOutput;
import com.epam.model.dto.TraineeUpdateDtoOutput;
import com.epam.model.dto.TraineeUpdateListDtoOutput;
import com.epam.model.dto.TrainerShortDtoInput;
import com.epam.model.dto.UserDtoInput;
import com.epam.model.dto.UserWithPassword;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepo traineeRepo;

    private final TrainerRepo trainerRepo;

    private final TraineeMapper traineeMapper;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    @Transactional
    @Counted("trainee_registration")
    public TraineeSaveDtoOutput save(TraineeDtoInput traineeDtoInput) {
        log.info("save, traineeDtoInput = {}", traineeDtoInput);

        UserWithPassword userWithPassword =
                userService.save(new UserDtoInput(traineeDtoInput.getFirstName(), traineeDtoInput.getLastName()));
        User user = userMapper.toEntity(userWithPassword);

        Trainee traineeToSave = traineeMapper.toEntity(traineeDtoInput);
        traineeToSave.setUser(user);

        Trainee trainee = traineeRepo.save(traineeToSave);

        return traineeMapper.toSaveDto(trainee, userWithPassword.getRawPassword());
    }

    @Override
    @Transactional
    public TraineeDtoOutput getByUsername(String username) {
        log.info("getByUsername, username = {}", username);

        User user = getUserByUsername(username);

        Trainee trainee = traineeRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));

        return traineeMapper.toDtoOutput(trainee);
    }

    @Override
    @Transactional
    public TraineeUpdateDtoOutput updateProfile(String username, TraineeProfileDtoInput traineeDtoInput) {
        log.info("updateProfile, traineeDtoInput = {}", traineeDtoInput);

        User user = getUserByUsername(username);

        Trainee trainee = traineeRepo.findByUserId(user.getId())
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
        traineeMapper.updateTraineeProfile(trainee, traineeDtoInput);

        Trainee updatedTrainee = traineeRepo.save(trainee);

        return traineeMapper.toTraineeUpdateDto(updatedTrainee);
    }

    @Override
    @Transactional
    public TraineeUpdateListDtoOutput updateTrainerList(String username, List<TrainerShortDtoInput> trainersUsernames) {
        log.info("updateTrainerList, trainersUsernames = {}", trainersUsernames);

        List<Trainer> selectedTrainers = trainerRepo.findAllByUser_UsernameIn(
                trainersUsernames.stream().map(TrainerShortDtoInput::getUsername).toList());
        Trainee trainee = traineeRepo.findByUser_Username(username)
                                     .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
        trainee.setTrainers(selectedTrainers);

        Trainee updatedTrainee = traineeRepo.save(trainee);

        return traineeMapper.toTraineeUpdateListDtoOutput(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.info("deleteByUsername, username = {}", username);

        User user = getUserByUsername(username);

        traineeRepo.deleteById(user.getId());
    }

    private User getUserByUsername(String username) {
        return userService.findUserByUsername(username)
                          .orElseThrow(() -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
    }
}
