/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance
 * with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html or
 * http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file and
 * include the License file at http://www.netbeans.org/cddl.txt. If applicable, add
 * the following below the CDDL Header, with the fields enclosed by brackets []
 * replaced by your own identifying information:
 * 
 *     "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 */

#ifndef _ExtractUtils_H
#define	_ExtractUtils_H

#include <windows.h>
#include "StringUtils.h"
#include "JavaUtils.h"
#include "Errors.h"

#ifdef	__cplusplus
extern "C" {
#endif    
    
    extern const DWORD STUB_FILL_SIZE;
    
    void skipStub(LauncherProperties * props);
    
    void loadI18NStrings(LauncherProperties * props);
    
    WCHARList * newWCHARList(DWORD number) ;
    void readLauncherProperties(LauncherProperties * props);    
    void freeWCHARList(WCHARList ** plist);
    void freeLauncherResource(LauncherResource ** file);
    
    void extractJVMData(LauncherProperties * props);
    void extractData(LauncherProperties *props);
    
#ifdef	__cplusplus
}
#endif

#endif	/* _ExtractUtils_H */
