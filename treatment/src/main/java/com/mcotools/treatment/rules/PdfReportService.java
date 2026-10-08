package com.mcotools.treatment.rules;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Genere le rapport de traitement (List d'ActionItem) au format PDF, sous
 * forme de tableau : entityType / id / action / reason / note.
 */
@Service
public class PdfReportService {

    private static final Logger log = LoggerFactory.getLogger(PdfReportService.class);

    private static final float MARGIN = 40f;
    private static final float FONT_SIZE = 9f;
    private static final float LINE_HEIGHT = 12f;
    private static final float ROW_PADDING = 6f;

    // Largeurs de colonnes en points, dans une page A4 paysage (~842pt de large)
    private static final Column[] COLUMNS = {
            new Column("Entite", 65f),
            new Column("Id", 55f),
            new Column("Action", 115f),
            new Column("Motif (reason)", 195f),
            new Column("Note - procedure a appliquer", 330f)
    };

    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDRectangle A4_LANDSCAPE = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

    public byte[] generatePdf(List<ActionItem> items) {
        try (PDDocument document = new PDDocument()) {
            PageContext ctx = new PageContext(document);
            newPage(document, ctx, true);

            for (ActionItem item : items) {
                String[] values = {
                        item.getEntityType(),
                        item.getId() != null ? String.valueOf(item.getId()) : "-",
                        item.getAction() != null ? item.getAction().name() : "-",
                        item.getReason() != null ? item.getReason() : "-",
                        item.getNote() != null ? item.getNote() : "-"
                };

                List<List<String>> wrappedPerColumn = new ArrayList<>();
                int maxLines = 1;
                for (int c = 0; c < COLUMNS.length; c++) {
                    List<String> lines = wrapText(values[c], FONT, FONT_SIZE, COLUMNS[c].width - 8f);
                    wrappedPerColumn.add(lines);
                    maxLines = Math.max(maxLines, lines.size());
                }
                float rowHeight = maxLines * LINE_HEIGHT + ROW_PADDING;

                if (ctx.y - rowHeight < MARGIN) {
                    ctx.stream.close();
                    newPage(document, ctx, true);
                }

                drawRow(ctx, wrappedPerColumn, maxLines, false);
            }

            ctx.stream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            log.info("Rapport PDF genere : {} ligne(s), {} page(s)", items.size(), document.getNumberOfPages());
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Erreur lors de la generation du PDF du rapport de traitement: {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible de generer le PDF du rapport", e);
        }
    }

    private void newPage(PDDocument document, PageContext ctx, boolean withHeader) throws IOException {
        PDPage page = new PDPage(A4_LANDSCAPE);
        document.addPage(page);
        ctx.stream = new PDPageContentStream(document, page);
        ctx.pageWidth = page.getMediaBox().getWidth();
        ctx.y = page.getMediaBox().getHeight() - MARGIN;

        if (ctx.pageCount == 0) {
            ctx.stream.beginText();
            ctx.stream.setFont(FONT_BOLD, 14f);
            ctx.stream.newLineAtOffset(MARGIN, ctx.y);
            ctx.stream.showText("Rapport de traitement MCOtools");
            ctx.stream.endText();
            ctx.y -= 20f;

            ctx.stream.beginText();
            ctx.stream.setFont(FONT, 9f);
            ctx.stream.newLineAtOffset(MARGIN, ctx.y);
            ctx.stream.showText("Genere le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            ctx.stream.endText();
            ctx.y -= 20f;
        }
        ctx.pageCount++;

        List<List<String>> header = new ArrayList<>();
        for (Column col : COLUMNS) {
            header.add(List.of(col.label));
        }
        drawRow(ctx, header, 1, true);
    }

    private void drawRow(PageContext ctx, List<List<String>> wrappedPerColumn, int maxLines, boolean isHeader) throws IOException {
        float rowHeight = maxLines * LINE_HEIGHT + ROW_PADDING;
        float x = MARGIN;
        PDFont font = isHeader ? FONT_BOLD : FONT;

        // separateur horizontal en bas de ligne
        ctx.stream.setLineWidth(0.5f);
        ctx.stream.moveTo(MARGIN, ctx.y - rowHeight);
        ctx.stream.lineTo(ctx.pageWidth - MARGIN, ctx.y - rowHeight);
        ctx.stream.stroke();

        for (int c = 0; c < COLUMNS.length; c++) {
            List<String> lines = wrappedPerColumn.get(c);
            float textY = ctx.y - LINE_HEIGHT;
            for (String line : lines) {
                ctx.stream.beginText();
                ctx.stream.setFont(font, FONT_SIZE);
                ctx.stream.newLineAtOffset(x + 4f, textY);
                ctx.stream.showText(line);
                ctx.stream.endText();
                textY -= LINE_HEIGHT;
            }
            x += COLUMNS[c].width;
        }
        ctx.y -= rowHeight;
    }

    /** Decoupe un texte en lignes ne depassant pas maxWidth pour la police/taille donnees. */
    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("-");
            return lines;
        }
        // Les retours a la ligne explicites (ex: requete SQL multi-lignes dans "note")
        // ne sont pas un glyphe affichable par PDFBox - on les traite comme des
        // frontieres de ligne avant le retour a la ligne automatique par mot.
        for (String rawLine : text.split("\n", -1)) {
            lines.addAll(wrapSingleLine(rawLine, font, fontSize, maxWidth));
        }
        return lines;
    }

    private List<String> wrapSingleLine(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text.isBlank()) {
            lines.add("");
            return lines;
        }
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            float width = font.getStringWidth(candidate) / 1000f * fontSize;
            if (width > maxWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static class Column {
        final String label;
        final float width;

        Column(String label, float width) {
            this.label = label;
            this.width = width;
        }
    }

    private static class PageContext {
        PDPageContentStream stream;
        float y;
        float pageWidth;
        int pageCount = 0;

        PageContext(PDDocument document) {
        }
    }
}
