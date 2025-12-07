package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.ReportFileFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportFileFormat fileFormat;

}