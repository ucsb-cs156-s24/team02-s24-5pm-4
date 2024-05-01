package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.R;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;
@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    @Operation(summary="List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests(){
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        return requests;
    }

    @Operation(summary = "Creates new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationDate(@Parameter(name = "requesterEmail") @RequestParam String requesterEmail,
                                                        @Parameter(name = "professorEmail") @RequestParam String professorEmail,
                                                        @Parameter(name = "explanation") @RequestParam String explanation,
                                                        @Parameter(name = "dateRequested") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
                                                        @Parameter(name = "dateNeeded") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
                                                        @Parameter(name = "done") @RequestParam boolean done) throws JsonProcessingException{
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequesterEmail(requesterEmail);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setExplanation(explanation);
        //NOTE FOR FUTURE USERS (likely me) SWAGGER UI SERIALIZES DATES, AND THAT CAUSE THE
        //CONTROLLER TO KICK THE REQUEST BACK! YOU MUST TREAT IT AS A STRING, AND THEN PARSE IT
        recommendationRequest.setDateRequested(/*LocalDateTime.parse(dateRequested)*/dateRequested);
        recommendationRequest.setDateNeeded(/*LocalDateTime.parse(dateNeeded)*/dateNeeded);
        recommendationRequest.setDone(done);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    @Operation(summary = "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(@Parameter(name = "id") @RequestParam Long id){
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));
        return recommendationRequest;
    }

    @Operation(summary = "Update a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(@Parameter(name="id") @RequestParam Long id, @RequestBody @Valid RecommendationRequest incoming){
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setRequesterEmail(incoming.getRequesterEmail());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setExplanation(incoming.getExplanation());
        recommendationRequest.setDateRequested(incoming.getDateRequested());
        recommendationRequest.setDateNeeded(incoming.getDateNeeded());
        recommendationRequest.setDone(incoming.getDone());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }

    @Operation(summary = "Delete a recommendation Request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(@Parameter(name = "id") @RequestParam Long id){
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id) );

        recommendationRequestRepository.delete(request);
        return genericMessage("Recommendation request with id %s deleted".formatted(id));
    }

}
