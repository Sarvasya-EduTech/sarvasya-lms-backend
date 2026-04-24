package com.sarvasya.sarvasya_lms_backend.service.result;

import com.sarvasya.sarvasya_lms_backend.model.result.Result;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public interface ResultService {
    List<Result> getAllResults();

    List<Result> getResultsByStudent(UUID studentId);

    Result saveResult(Result result);

    void deleteResult(UUID id);

    byte[] generatePdf(UUID resultId, UUID userId) throws Exception;

}








