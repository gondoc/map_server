package com.gondo.map.application.api;

import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.common.CommResponse;
import com.gondo.map.domain.hist.dto.YearHistDto;
import com.gondo.map.domain.hist.record.HistRecord;
import com.gondo.map.domain.hist.record.HistSaveRecord;
import com.gondo.map.domain.hist.record.HistUpdateRecord;
import com.gondo.map.domain.hist.service.HistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hist")
public class HistController {

    private final HistService histService;

    @Value("${api.use}")
    private String apiUse;

    @GetMapping("/items")
    public CommResponse<List<HistRecord>> getItems() {
        return CommResponse.response200(histService.getItems());
    }

    @GetMapping("/year-items")
    public CommResponse<List<YearHistDto>> getItemsBy() {
        return CommResponse.response200(histService.getYearItems());
    }

    @PostMapping("/item")
    public CommResponse<HistRecord> addItem(@RequestBody HistSaveRecord saveRecord) {
        return Boolean.parseBoolean(apiUse) ? CommResponse.response200(histService.addItem(saveRecord)) :
                CommResponse.response405(null);
    }

    @PatchMapping("/item")
    public CommResponse<?> modItem(@RequestBody HistUpdateRecord updateRecord) {
        try {
            return Boolean.parseBoolean(apiUse) ? CommResponse.response200(histService.updateItem(updateRecord)) :
                    CommResponse.response405(null);
        } catch (CommException e) {
            return CommResponse.response405(e.getMessage());
        } catch (Exception e) {
            return CommResponse.response500(null);
        }
    }

    @DeleteMapping("/item/{id}")
    public CommResponse<?> delItem(@PathVariable("id") String id) {
        try {
            return Boolean.parseBoolean(apiUse) ? CommResponse.response200(histService.deleteItem(id)) :
                    CommResponse.response405(null);
        } catch (CommException e) {
            return CommResponse.response405(e.getMessage());
        } catch (Exception e) {
            return CommResponse.response500(null);
        }
    }
}
