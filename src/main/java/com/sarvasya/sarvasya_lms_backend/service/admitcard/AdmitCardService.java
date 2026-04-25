package com.sarvasya.sarvasya_lms_backend.service.admitcard;

import com.sarvasya.sarvasya_lms_backend.model.admitcard.AdmitCard;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

public interface AdmitCardService {
    List<AdmitCard> getAllAdmitCards();

    List<AdmitCard> getAdmitCardsByClass(UUID classId);

    AdmitCard createAdmitCard(AdmitCard admitCard);

    void deleteAdmitCard(UUID id);

    byte[] generatePdf(UUID admitCardId, UUID userId) throws Exception;

}








