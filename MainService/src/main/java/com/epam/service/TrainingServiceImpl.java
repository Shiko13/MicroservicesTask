package com.epam.service;

import com.epam.error.AccessException;
import com.epam.error.ErrorMessageConstants;
import com.epam.mapper.TrainingMapper;
import com.epam.model.Training;
import com.epam.model.dto.ActionType;
import com.epam.model.dto.TrainerWorkloadDto;
import com.epam.model.dto.TrainingDtoInput;
import com.epam.model.dto.TrainingForTraineeDtoOutput;
import com.epam.model.dto.TrainingForTrainerDtoOutput;
import com.epam.repo.TraineeRepo;
import com.epam.repo.TrainerRepo;
import com.epam.repo.TrainingRepo;
import com.epam.spec.TrainingTraineeSpecification;
import com.epam.spec.TrainingTrainerSpecification;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepo trainingRepo;

    private final TraineeRepo traineeRepo;

    private final TrainerRepo trainerRepo;

    private final TrainingMapper trainingMapper;

    private final RestTemplate restTemplate;

    private final EurekaClient eurekaClient;

    private final TokenExtractorService tokenExtractorService;

    @Override
    @Transactional
    public Training save(TrainingDtoInput trainingDtoInput, HttpServletRequest request) {
        log.info("save, trainingDtoInput = {}", trainingDtoInput);

        var trainingToSave = trainingMapper.toEntity(trainingDtoInput);
        var trainee = traineeRepo.findByUser_Username(trainingDtoInput.getTraineeUsername())
                                 .orElseThrow(() -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));
        var trainer = trainerRepo.findByUser_Username(trainingDtoInput.getTrainerUsername())
                                 .orElseThrow(() -> new AccessException(ErrorMessageConstants.ACCESS_ERROR_MESSAGE));

        trainingToSave.setTrainee(trainee);
        trainingToSave.setTrainer(trainer);

        var savedTraining = trainingRepo.save(trainingToSave);

        sendRequest(savedTraining, ActionType.POST, request);

        return savedTraining;
    }

    @Override
    @Timed("findByDateRangeAndTraineeUsernameTime")
    public List<TrainingForTraineeDtoOutput> findByDateRangeAndTraineeUsername(
            TrainingTraineeSpecification specification) {

        log.info("findByDateRangeAndTraineeUsername, specification = {}", specification);

        var trainings = trainingRepo.findAll(specification);

        return trainingMapper.toTrainingForTraineeDtoList(trainings);
    }

    @Override
    @Timed("findByDateRangeAndTrainerUsernameTime")
    public List<TrainingForTrainerDtoOutput> findByDateRangeAndTrainerUsername(
            TrainingTrainerSpecification specification) {
        log.info("findByDateRangeAndTrainerUsername, specification = {}", specification);

        var trainings = trainingRepo.findAll(specification);

        return trainingMapper.toTrainingForTrainerDtoList(trainings);
    }

    @Override
    @Transactional
    public void deleteById(Long id, HttpServletRequest request) {
        log.info("deleteById, id = {}", id);

        var training = getTrainingById(id);

        trainingRepo.delete(training);

        sendRequest(training, ActionType.DELETE, request);
    }

    private Training getTrainingById(Long id) {
        return trainingRepo.findById(id)
                           .orElseThrow(() -> new AccessException(ErrorMessageConstants.NOT_FOUND_MESSAGE));
    }

    public void sendRequest(Training training, ActionType actionType, HttpServletRequest request) {
        TrainerWorkloadDto trainerDto = createTrainerDto(training, actionType);
        String token = tokenExtractorService.extractToken(request);
        HttpHeaders httpHeaders = createHttpHeaders(token);
        String url = buildServiceUrl();

        if (url != null) {
            HttpEntity<TrainerWorkloadDto> requestEntity = createRequestEntity(trainerDto, httpHeaders);
            sendHttpRequest(url, requestEntity);
        } else {
            log.warn("No valid Eureka instance found.");
        }
    }

    private TrainerWorkloadDto createTrainerDto(Training training, ActionType actionType) {
        return TrainerWorkloadDto.builder()
                                 .username(training.getTrainer().getUser().getUsername())
                                 .firstName(training.getTrainer().getUser().getFirstName())
                                 .lastName(training.getTrainer().getUser().getLastName())
                                 .isActive(training.getTrainer().getUser().getIsActive())
                                 .date(training.getDate())
                                 .duration(training.getDuration())
                                 .actionType(actionType)
                                 .build();
    }

    private HttpHeaders createHttpHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        return httpHeaders;
    }

    private String buildServiceUrl() {
        InstanceInfo instance = eurekaClient.getNextServerFromEureka("spring-cloud-eureka-statistic-client", false);
        return (instance != null) ? instance.getHomePageUrl() + "/trainer-another" : null;
    }

    private HttpEntity<TrainerWorkloadDto> createRequestEntity(TrainerWorkloadDto trainerDto, HttpHeaders httpHeaders) {
        return new HttpEntity<>(trainerDto, httpHeaders);
    }

    private void sendHttpRequest(String url, HttpEntity<TrainerWorkloadDto> requestEntity) {
        try {
            restTemplate.postForEntity(url, requestEntity, TrainerWorkloadDto.class);
        } catch (Exception e) {
            log.error("Error while sending HTTP request", e);
        }
    }
}
