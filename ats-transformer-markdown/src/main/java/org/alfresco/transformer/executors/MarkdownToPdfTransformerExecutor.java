/*
 * #%L
 * Alfresco Transform Service Transformer
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.transformer.executors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.alfresco.transform.exceptions.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.qkyrie.markdown2pdf.Markdown2PdfConverter;
import com.qkyrie.markdown2pdf.internal.reading.SimpleStringMarkdown2PdfReader;
import com.qkyrie.markdown2pdf.internal.writing.SimpleFileMarkdown2PdfWriter;

@Component
public class MarkdownToPdfTransformerExecutor implements JavaExecutor {
	
    @Autowired
    public MarkdownToPdfTransformerExecutor() {
    }

    @Override
    public void call(File sourceFile, File targetFile, String... args) throws TransformException {
        
        try
        {
            Markdown2PdfConverter.newConverter()
                    .readFrom(new SimpleStringMarkdown2PdfReader(
                            new String(Files.readAllBytes(Paths.get(sourceFile.getPath())))))
                    .writeTo(new SimpleFileMarkdown2PdfWriter(targetFile))
                    .doIt();
        }
        catch (Exception e)
        {
            throw new TransformException(HttpStatus.INTERNAL_SERVER_ERROR.value(), MarkdownToPdfTransformerExecutor.getMessage(e));
        }
        
        if (!targetFile.exists() || targetFile.length() == 0) {
            throw new TransformException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Transformer failed to create an output file");
        }

    }
    
    private static String getMessage(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
    
}
