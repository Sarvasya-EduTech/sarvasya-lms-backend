package com.sarvasya.sarvasya_lms_backend.service.impl.admitcard;

import com.sarvasya.sarvasya_lms_backend.model.admitcard.AdmitCard;
import com.sarvasya.sarvasya_lms_backend.model.classes.Classes;
import com.sarvasya.sarvasya_lms_backend.model.user.User;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.repository.admitcard.AdmitCardRepository;
import com.sarvasya.sarvasya_lms_backend.service.admitcard.AdmitCardService;
import com.sarvasya.sarvasya_lms_backend.service.classes.ClassesService;
import com.sarvasya.sarvasya_lms_backend.service.user.UserService;

@Service
@RequiredArgsConstructor
public class AdmitCardServiceImpl implements AdmitCardService {

    private final AdmitCardRepository admitCardRepository;
    private final UserService userService;
    private final ClassesService classesService;

    public List<AdmitCard> getAllAdmitCards() {
        return admitCardRepository.findAll();
    }

    public List<AdmitCard> getAdmitCardsByClass(UUID classId) {
        return admitCardRepository.findByClassId(classId);
    }

    @Transactional
    public AdmitCard createAdmitCard(AdmitCard admitCard) {
        return admitCardRepository.save(admitCard);
    }

    @Transactional
    public void deleteAdmitCard(UUID id) {
        admitCardRepository.deleteById(id);
    }

    public byte[] generatePdf(UUID admitCardId, UUID userId) throws Exception {
        AdmitCard admitCard = admitCardRepository.findById(admitCardId)
                .orElseThrow(() -> new RuntimeException("Admit Card not found"));
        User student = userService.findById(userId);
        if (student == null) throw new RuntimeException("Student not found");
        
        Classes clazz = classesService.findById(admitCard.getClassId());
        String className = clazz != null ? clazz.getSubject() : "N/A";

        String examRows = admitCard.getExams().stream()
                .map(e -> "<tr><td>" + e.getSubject() + "</td><td>" + (e.getTotalMarks() != null ? e.getTotalMarks() : "N/A") + "</td></tr>")
                .collect(Collectors.joining());

        String html = "<!DOCTYPE html><html><head><style>" +
                "body { font-family: sans-serif; padding: 40px; color: #333; }" +
                ".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 30px; }" +
                ".details { margin-bottom: 30px; }" +
                ".details p { margin: 5px 0; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }" +
                "th { background-color: #f4f4f4; }" +
                ".footer { margin-top: 50px; font-size: 12px; color: #777; border-top: 1px solid #eee; padding-top: 10px; }" +
                "</style></head><body>" +
                "<div class='header'><h1>EXAMINATION ADMIT CARD</h1><h3>Sarvasya Educational Institution</h3></div>" +
                "<div class='details'>" +
                "<p><strong>Student Name:</strong> " + student.getName() + "</p>" +
                "<p><strong>Email:</strong> " + student.getEmail() + "</p>" +
                "<p><strong>Class:</strong> " + className + "</p>" +
                "</div>" +
                "<table><thead><tr><th>Subject</th><th>Total Marks</th></tr></thead><tbody>" +
                examRows +
                "</tbody></table>" +
                "<div class='footer'><p>This is a computer-generated document. No signature required.</p>" +
                "<p>Please bring a printed copy to the examination hall along with a valid ID proof.</p></div>" +
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








