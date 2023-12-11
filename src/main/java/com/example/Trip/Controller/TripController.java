package com.example.Trip.Controller;

import com.example.Trip.Dto.MainItemDto;
import com.example.Trip.Dto.TripDto;
import com.example.Trip.Dto.TripFormDto;
import com.example.Trip.Dto.TripSearchDto;
import com.example.Trip.Entity.Trip;
import com.example.Trip.Repository.TripImgRepository;
import com.example.Trip.Repository.TripRepository;
import com.example.Trip.Service.TripService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class TripController {
    private final TripService tripService;
    private final TripRepository tripRepository;
    private final TripImgRepository tripImgRepository;


    //여행지 전체 리스트
    @GetMapping("/trips")
    public String getTrips(Model model) {
        List<TripDto> trips = tripService.findAll()
                .stream()
                .map(TripDto::new)
                .toList();
        model.addAttribute("trips", trips);

        return "trip/tripPage";
    }


//    //여행지역별로 여행지 검색
//    @GetMapping("/trips/address") //페이지가 있는경우와 없는경우
//    public String getTripsByAddressAndName(@RequestParam(value = "query", required = false) String query,
//                                           @PageableDefault(page = 0, size = 10) Pageable pageable,
//                                           Model model) {
//        Page<Trip> tripsByAddressOrName = tripService.findByAddressOrName(query, pageable);
//        model.addAttribute("trips", tripsByAddressOrName);
//        return "trip/tripPage";
//    }

    //조건별로 여행지 페이징처리
    @GetMapping(value = {"/main/trips", "/main/trips/{page}"})
    public String tripManage(TripSearchDto tripSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        Page<MainItemDto> trips = tripService.getTripPage(tripSearchDto, pageable);
        model.addAttribute("trips", trips);
        model.addAttribute("tripSearchDto", tripSearchDto);
        model.addAttribute("maxPage", 5);
        return "trip/tripPage";
    }


    //여행지 등록
    @GetMapping(value = "/admin/trip/new")
    public String tripForm(Model model) {
        model.addAttribute("tripFormDto", new TripFormDto());
        return "trip/tripForm";
    }

    @PostMapping(value = "/admin/trip/new")
    public String tripNew(@Valid TripFormDto tripFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("tripImgFile") List<MultipartFile> tripImgFileList) {
        if (bindingResult.hasErrors()) {
            return "trip/tripForm";
        }
        //첫번째 이미지를 등록하지 않았을 때
        if (tripImgFileList.get(0).isEmpty() && tripFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 이미지는 필수 입력 값입니다.");
            return "trip/tripForm";
        }
        try {
            tripService.saveTrip(tripFormDto, tripImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "여행지 등록중 오류 발생");
            return "trip/tripForm";
        }
        return "redirect:/";
    }


    // 여행지 상세 보기
    @GetMapping(value = "/admin/trip/{id}")
    public String tripDtl(@PathVariable("id") Long id, Model model) {
        try {
            TripFormDto tripFormDto = tripService.getTripDtl(id);
            model.addAttribute("trip", tripFormDto);

        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 여행지 입니다.");
            model.addAttribute("tripFormDto", new TripFormDto());

            return "trip/trip";
        }
        return "trip/trip";
    }


    //여행지 수정
    @PostMapping(value = "/admin/trip/edit/{id}")
    public String tripUpdate(@Valid TripFormDto tripFormDto, BindingResult bindingResult, @RequestParam("tripImgFile") List<MultipartFile> tripImgFileList, Model model) {
        if (bindingResult.hasErrors()) {
            return "trip/tripForm";
        }
        if (tripImgFileList.get(0).isEmpty() && tripFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 이미지는 필수 입력값입니다.");
            return "trip/tripForm";
        }
        try {
            tripService.updateTrip(tripFormDto, tripImgFileList);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 에러가 발생");
            return "trip/tripForm";
        }
        return "redirect:/";
    }


    //여행지 수정
    @GetMapping("/admin/trip/edit/{id}")
    public String showEditTripForm(@PathVariable Long id, Model model) {
        Trip trip = tripService.findById(id);
        model.addAttribute("trip", trip);
        return "trip/tripForm";
    }


//    @GetMapping("/admin/trip/edit/{id}")
//    public String showUpdatedTripPage(@PathVariable Long id, Model model) {
//        // Retrieve the updated trip by id
//        Trip updatedTrip = tripRepository.findById(id)
//                .orElseThrow(EntityNotFoundException::new);
//
//        // Add the updated trip to the model
//        model.addAttribute("trip", updatedTrip);
//
//        // Add any other necessary data to the model
//
//        return "trip/updatedTripPage"; // Update the view name based on your file structure
//    }



    //여행지 삭제
    @Transactional
    @RequestMapping(value = {"/admin/trip/delete/{id}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteById(@PathVariable Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        tripImgRepository.deleteByTrip(trip);
        tripRepository.deleteById(id);

        return "redirect:/";
    }


}
