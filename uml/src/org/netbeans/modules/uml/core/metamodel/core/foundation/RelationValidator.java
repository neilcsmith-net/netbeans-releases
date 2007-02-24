/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.uml.core.metamodel.core.foundation;

import org.netbeans.modules.uml.core.coreapplication.ICoreProduct;
import org.netbeans.modules.uml.core.eventframework.EventDispatchNameKeeper;
import org.netbeans.modules.uml.core.eventframework.IEventDispatchController;
import org.netbeans.modules.uml.core.eventframework.IEventDispatcher;
import org.netbeans.modules.uml.core.eventframework.IEventPayload;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IClassifier;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IGeneralization;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IImplementation;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IInterface;
import org.netbeans.modules.uml.core.support.umlsupport.ProductRetriever;
import org.netbeans.modules.uml.core.support.umlutils.ETList;

/**
 * RelationValidator manages the dispatch of relation validation events, while
 * also having the meta layer validate the proposed relationship against UML
 * well-formedness rules.
 */
public class RelationValidator {

	/**
	 *
	 */
	public RelationValidator() {
		super();
	}

	/**
	 *
	 * Called to validate the proposed relationship. Calling this
	 * method will result in the firing of the IRelationValidatorEventsSink
	 * methods.
	 *
	 * @param proxy[in] The proxy to validate
	 *
	 * @return HRESULT
	 *
	 */
	public boolean validateRelation( IRelationProxy proxy )
	{
		boolean valid = false;
		if (proxy != null)
		{
			proxy.setRelationValidated(false);
			ICoreProduct prod = ProductRetriever.retrieveProduct();
			if (prod != null)
			{
				IEventDispatchController cont = prod.getEventDispatchController();
				if (cont != null)
				{
					IEventDispatcher disp = cont.retrieveDispatcher(EventDispatchNameKeeper.relation());
					if (disp != null && disp instanceof IRelationValidatorEventDispatcher)
					{
						IRelationValidatorEventDispatcher rDisp = (IRelationValidatorEventDispatcher)disp;
						IEventPayload payload = rDisp.createPayload("PreRelationValidate");
						boolean proceed = rDisp.firePreRelationValidate(proxy, payload);
						if (proceed)
						{
							proceed = metaLayerValidation(proxy);
							if (proceed)
							{
								// Only fire the RelationValidated event if the meta layer
								// ok'd the relation. This will prevent any data corruption
								// issues in regard to relationship building
								proxy.setRelationValidated(true);
								payload = rDisp.createPayload("RelationValidated");
								rDisp.fireRelationValidated(proxy, payload);
                                                                valid = true;
							}
						}
					}
				}
			}
		}
		return valid;
	}

	/**
	 *
	 * Validates the proposed relation against UML well-formedness rules.
	 *
	 * @param proxy[in] The proxy to validated
	 * @param proceed[out] true if validated, else false
	 *
	 * @return HRESULT
	 *
	 */
	protected boolean metaLayerValidation(IRelationProxy proxy) {
		boolean proceed = true;
		IElement fromEl = proxy.getFrom();
		IElement toEl = proxy.getTo();
		proceed = validateRels(fromEl, toEl, proxy);
		if (proceed)
		{
			MetaLayerRelationFactory relFact = MetaLayerRelationFactory.instance();
			IDirectedRelationship impStmt = null;
			boolean isNeeded = relFact.isImportNeeded(fromEl, toEl);
			if (isNeeded)
			{
				impStmt = relFact.establishImportIfNeeded(fromEl, toEl);
			}
		}
		return proceed;
	}

	/**
	 *
	 * Does basic relation validation, looking for circular generalizations, etc.
	 *
	 * @param pFromEl[in]   The from element
	 * @param pToEl[in]     The to element
	 * @param proxy[in]     The RelationProxy
	 *
	 * @return HRESULT
	 *
	 */
	protected boolean validateRels(IElement fromEl, IElement toEl, IRelationProxy proxy) {
		// Initialize the validated flag so that we assume the relationship is valid until
		// we determine it isn't.
		boolean validated = true;
		if (fromEl != null && toEl != null && proxy != null)
		{
			String relType = proxy.getConnectionElementType();
			if (relType != null && relType.equals("Generalization"))
			{
				// Need to make sure we don't have a circular generalization link.
				if (toEl instanceof IClassifier)
				{
					IClassifier classifier = (IClassifier)toEl;
					if (matchesSuper(classifier, fromEl))
					{
						validated = false;
					}
				}
			}
			if (validated)
			{
				if (relType.length() > 0 && !relType.equals("Association")
				&& !relType.equals("Aggregation") && !relType.equals("Composition"))
				{
					// in other words, only associations can exist more than once between the objects
					IClassifier pFrom = null;
					if (fromEl instanceof IClassifier)
					{
						pFrom = (IClassifier)fromEl;
					}
					IClassifier pTo = null;
					if (toEl instanceof IClassifier)
					{
						pTo = (IClassifier)toEl;
					}
					if (relType.equals("Generalization") && pFrom != null && pTo != null)
					{
						if (generalizationExists(pFrom, pTo))
						{
							validated = false;
						}
					}

					if (validated && relType.equals("Implementation")
						&& pFrom != null && pTo != null)
					{
						if (implementationExists(pFrom, pTo))
						{
							validated = false;
						}
					}
					// Now we have all flavors of dependency, but each one can exist?
					// TODO: Limit dependencies?
				}
			}
		}
		return validated;
	}

	/**
	 *
	 * Determines if any super class of classifier matches elementToMatch
	 *
	 * @param classifier[in]      The classifier whose super classes we are matching against elementToMatch
	 * @param elementToMatch[in]  The element to match against
	 *
	 * @return HRESULT
	 *
	 */
	protected boolean matchesSuper( IClassifier classifier, IElement elementToMatch )
	{
		boolean matches = false;
		if (classifier != null && elementToMatch != null)
		{
			ETList<IGeneralization> gens = classifier.getGeneralizations();
			if (gens != null)
			{
				int count = gens.size();
				for (int i=0; i<count; i++)
				{
					IGeneralization gen = gens.get(i);
					IClassifier supCl = gen.getGeneral();
					if (supCl != null)
					{
						boolean isSame = false;
						isSame = supCl.isSame(elementToMatch);
						if (isSame)
						{
							matches = true;
							break;
						}
						else
						{
							matches = matchesSuper(supCl, elementToMatch);
						}
					}
				}
			}
		}
		return matches;
	}

	/**
	 *
	 * Checks to see if a generalization relationship already exists between
	 * the two classifers.
	 *
	 * @param pFrom[in] The candidate specialing classifier
	 * @param pTo[in] The candidate base classifier
	 *
	 * @return true if the same relation already exists
	 *
	 */
	protected boolean generalizationExists ( IClassifier pFrom, IClassifier pTo )
	{
		boolean retVal = false;
		if (pFrom != null && pTo != null)
		{
			// always go from the "from" class. Usually not any multiple inheritances
			ETList<IGeneralization> gens = pFrom.getGeneralizations();
			if (gens != null)
			{
				int count = gens.size();
				int idx = 0;
				while ( idx < count && retVal == false )
				{
					IGeneralization gen = gens.get(idx);
					IClassifier pSuper = gen.getGeneral();
					if (pSuper != null)
					{
						boolean isSame = false;
						isSame = pTo.isSame(pSuper);
						if (isSame)
						{
							retVal = true;
						}
					}
					idx++;
				}
			}
		}
		return retVal;
	}

	/**
	 *
	 * Checks to see if an implementation relationship already exists between
	 * the two classifers.
	 *
	 * @param pFrom[in] The candidate implementing classifier
	 * @param pTo[in] The candidate interface
	 *
	 * @return true if the same relation already exists
	 *
	 */
	protected boolean implementationExists ( IClassifier pFrom, IClassifier pTo )
	{
		boolean retVal = false;
		if (pFrom != null && pTo != null)
		{
			// always go from the "from" class. Usually not many multiple implementations
			ETList<IImplementation> imps = pFrom.getImplementations();
			if (imps != null)
			{
				int count = imps.size();
				int idx = 0;
				while ( idx < count && retVal == false )
				{
					IImplementation imp = imps.get(idx);
					IInterface pIFace = imp.getContract();
					if (pIFace != null)
					{
						boolean isSame = false;
						isSame = pTo.isSame(pIFace);
						if (isSame)
						{
							retVal = true;
						}
					}
					idx++;
				}
			}
		}
		return retVal;
	}


}

