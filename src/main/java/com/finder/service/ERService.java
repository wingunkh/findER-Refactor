package com.finder.service;

import com.finder.api.KakaoMobilityAPIService;
import com.finder.domain.ER;
import com.finder.dto.ERDetailDto;
import com.finder.dto.ERPreviewDto;
import com.finder.dto.MarkerResponseDto;
import com.finder.repository.ERRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ERService {
    private final ERRepository erRepository;
    private final BedService bedService;
    private final KakaoMobilityAPIService kakaoMobilityAPIService;

    @Transactional(readOnly = true)
    public List<MarkerResponseDto> findAllERLocation() {
        return erRepository.findAll().stream()
                .map(er -> MarkerResponseDto.builder()
                        .hpID(er.getHpID())
                        .lat(er.getLatitude())
                        .lon(er.getLongitude())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ERPreviewDto findERPreview(String hpID, Double lat, Double lon) {
        ER er = erRepository.findById(hpID)
                .orElseThrow(() -> new IllegalArgumentException("응급실이 존재하지 않습니다."));

        // 병상 수, 병상 갱신 시간 조회
        Map<String, Object> bedInfo = bedService.getBedCountAndBedTime(hpID);
        // 거리, 도착 예정 시간 조회
        Map<String, String> distanceInfo = kakaoMobilityAPIService.getDistanceAndETA(lat, lon, er.getLatitude(), er.getLongitude());

        return ERPreviewDto.builder()
                .hpID(er.getHpID())
                .name(er.getName())
                .address(er.getAddress())
                .tel(er.getTel())
                .ERTel(er.getERTel())
                .bedCount((Integer) bedInfo.get("bedCount"))
                .bedTime((String) bedInfo.get("bedTime"))
                .distance(Double.parseDouble(distanceInfo.get("distance")))
                .ETA(distanceInfo.get("ETA"))
                .build();
    }

    @Transactional(readOnly = true)
    public ERDetailDto findERDetail(String hpID, Double lat, Double lon) {
        ER er = erRepository.findById(hpID)
                .orElseThrow(() -> new IllegalArgumentException("응급실이 존재하지 않습니다."));

        // 병상 수, 병상 갱신 시간 조회
        Map<String, Object> bedInfo = bedService.getBedCountAndBedTime(hpID);
        // 거리, 도착 예정 시간 조회
        Map<String, String> distanceInfo = kakaoMobilityAPIService.getDistanceAndETA(lat, lon, er.getLatitude(), er.getLongitude());

        return ERDetailDto.builder()
                .name(er.getName())
                .address(er.getAddress())
                .mapAddress(er.getMapAddress() == null ? "" : er.getMapAddress())
                .tel(er.getTel())
                .ERTel(er.getERTel())
                .ambulance(er.getAmbulance())
                .CT(er.getCT())
                .MRI(er.getMRI())
                .latitude(er.getLatitude())
                .longitude(er.getLongitude())
                .subject(er.getSubject())
                .bedCount((Integer) bedInfo.get("bedCount"))
                .bedTime((String) bedInfo.get("bedTime"))
                .distance(Double.parseDouble(distanceInfo.get("distance")))
                .ETA(distanceInfo.get("ETA"))
                .build();
    }
}
