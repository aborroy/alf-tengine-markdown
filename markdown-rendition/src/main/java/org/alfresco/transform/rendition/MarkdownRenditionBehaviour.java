package org.alfresco.transform.rendition;

import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rendition2.RenditionDefinitionRegistry2;
import org.alfresco.repo.rendition2.RenditionService2;
import org.alfresco.rest.framework.core.exceptions.InvalidArgumentException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performs PDF renditions on created and modified Markdown content nodes using Rendition Service V2.
 * Share Web App is still using Rendition Service V1, so in order to get the rendition done with this
 * new transformer this additional operation is required.
 *  
 * @author aborroy
 *
 */
public class MarkdownRenditionBehaviour implements NodeServicePolicies.OnCreateNodePolicy, ContentServicePolicies.OnContentUpdatePolicy
{
    
    private static final Log LOG = LogFactory.getLog(MarkdownRenditionBehaviour.class);
    
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private RenditionService2 renditionService2;
    
    public void init() {
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_CONTENT,
                new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(
                ContentServicePolicies.OnContentUpdatePolicy.QNAME,
                ContentModel.TYPE_CONTENT,
                new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        requestPdfRendition(nodeRef);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        requestPdfRendition(childAssocRef.getChildRef());
    }
    
    /**
     * Performs a PDF Rendition on sourceNodeRef if mime type is Markdown
     * @param sourceNodeRef The node to be renditioned
     */
    private void requestPdfRendition(NodeRef sourceNodeRef)
    {
        if (nodeService.exists(sourceNodeRef))
        {
            ContentData contentData = (ContentData) nodeService.getProperty(sourceNodeRef, ContentModel.PROP_CONTENT);
            if (contentData.getMimetype().equals("text/x-markdown") || contentData.getMimetype().equals("text/markdown"))
            {
                String mimeType = contentData.getMimetype();
                long size = contentData.getSize();
                RenditionDefinitionRegistry2 renditionDefinitionRegistry2 = renditionService2.getRenditionDefinitionRegistry2();
                Set<String> availableRenditions = renditionDefinitionRegistry2.getRenditionNamesFrom(mimeType, size);
    
                try
                {
                    if (!availableRenditions.contains("pdf"))
                    {
                        throw new InvalidArgumentException("Unable to create rendition 'pdf' for " + mimeType
                                + " as no transformer is currently available.");
                    }
                    renditionService2.render(sourceNodeRef, "pdf");
                } catch (Exception ex)
                {
                    // Don't throw the exception as we don't want the the upload to fail, just log it
                    LOG.warn("Asynchronous request to create a rendition upon upload failed: " + ex.getMessage());
                }
            }
        }

    }

    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setRenditionService2(RenditionService2 renditionService2)
    {
        this.renditionService2 = renditionService2;
    }
}
