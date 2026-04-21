package com.sarvasya.sarvasya_lms_backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sarvasya.sarvasya_lms_backend.model.Result;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.model.Exam;
import com.sarvasya.sarvasya_lms_backend.repository.ResultRepository;
import com.sarvasya.sarvasya_lms_backend.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final ExamRepository examRepository;
    private final UserService userService;

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    public List<Result> getResultsByStudent(UUID studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    @Transactional
    public Result saveResult(Result result) {
        if (result.getItems() != null) {
            for (com.sarvasya.sarvasya_lms_backend.model.ResultItem item : result.getItems()) {
                item.setResult(result);
            }
        }
        return resultRepository.save(result);
    }

    @Transactional
    public void deleteResult(UUID id) {
        resultRepository.deleteById(id);
    }

    public byte[] generatePdf(UUID resultId, UUID userId) throws Exception {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));
        
        User student = userService.findById(userId);
        if (student == null) throw new RuntimeException("Student not found");
        
        StringBuilder itemsHtml = new StringBuilder();
        itemsHtml.append("<table style='width:100%; border-collapse: collapse; margin-top:20px;'>");
        itemsHtml.append("<thead><tr style='background-color:#eee;'>")
                 .append("<th style='border:1px solid #ccc; padding:8px; text-align:left;'>Subject / Exam</th>")
                 .append("<th style='border:1px solid #ccc; padding:8px; text-align:center;'>Marks Obtained</th>")
                 .append("<th style='border:1px solid #ccc; padding:8px; text-align:center;'>Total Marks</th>")
                 .append("</tr></thead><tbody>");

        if (result.getItems() != null) {
            for (com.sarvasya.sarvasya_lms_backend.model.ResultItem item : result.getItems()) {
                Exam exam = examRepository.findById(item.getExamId()).orElse(null);
                String subject = (exam != null) ? exam.getSubject() : "Unknown Subject";
                itemsHtml.append("<tr>")
                         .append("<td style='border:1px solid #ccc; padding:8px;'>").append(subject).append("</td>")
                         .append("<td style='border:1px solid #ccc; padding:8px; text-align:center;'>").append(item.getMarksObtained()).append("</td>")
                         .append("<td style='border:1px solid #ccc; padding:8px; text-align:center;'>").append(item.getTotalMarks()).append("</td>")
                         .append("</tr>");
            }
        }
        itemsHtml.append("</tbody></table>");

        String html = "<!DOCTYPE html><html><head><style>" +
                "body { font-family: sans-serif; padding: 40px; color: #333; }" +
                ".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 30px; }" +
                ".details { margin-bottom: 30px; }" +
                ".details p { margin: 5px 0; }" +
                ".result-box { border: 2px solid #333; padding: 20px; text-align: center; background-color: #f9f9f9; }" +
                ".score { font-size: 20px; font-weight: bold; margin: 20px 0; text-align: right; }" +
                ".grade { font-size: 28px; color: #2c3e50; font-weight: bold; text-align: center; margin-top: 10px; }" +
                ".footer { margin-top: 50px; font-size: 12px; color: #777; border-top: 1px solid #eee; padding-top: 10px; }" +
                "</style></head><body>" +
                "<div class='header'><h1>ACADEMIC PERFORMANCE REPORT</h1><h3>Sarvasya Educational Institution</h3></div>" +
                "<div class='details'>" +
                "<p><strong>Student Name:</strong> " + student.getName() + "</p>" +
                "<p><strong>Email:</strong> " + student.getEmail() + "</p>" +
                "</div>" +
                "<div>" +
                "<h3>MARKS STATEMENT</h3>" +
                itemsHtml.toString() +
                "</div>" +
                "<div class='score'>Aggregate: " + result.getMarksObtained() + " / " + result.getTotalMarks() + "</div>" +
                (result.getGrade() != null && !result.getGrade().isEmpty() ? "<div class='grade'>Overall Grade: " + result.getGrade() + "</div>" : "") +
                "<div class='footer'><p>This is a computer-generated document. No signature required.</p>" +
                "<p>Date of issue: " + java.time.LocalDate.now() + "</p></div>" +
                "</body></html>";

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
