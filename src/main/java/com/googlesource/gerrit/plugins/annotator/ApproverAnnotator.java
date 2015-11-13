// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.annotator;

import com.google.gerrit.common.data.LabelTypes;
import com.google.gerrit.common.data.Permission;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.SuggestedReviewerInfo;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.change.SuggestReviewers.ReviewerAnnotator;
import com.google.gerrit.server.project.ChangeControl;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class ApproverAnnotator implements ReviewerAnnotator {
  private static final String codeReviewLabelName = "Code-Review";
  private static String codeReviewPermission;

  public static class Module extends AbstractModule {
    @Override
    protected void configure() {
      codeReviewPermission = Permission.forLabel(codeReviewLabelName);

      DynamicItem.bind(binder(), ReviewerAnnotator.class)
          .to(ApproverAnnotator.class);
    }
  }

  private final AccountCache accountCache;
  private final IdentifiedUser.GenericFactory userFactory;

  @Inject
  ApproverAnnotator(AccountCache accountCache,
      IdentifiedUser.GenericFactory userFactory) {
    this.accountCache = accountCache;
    this.userFactory = userFactory;
  }

  @Override
  public String getAnnotation(SuggestedReviewerInfo ri, ChangeControl cc) {
    if (isApprover(ri, cc)) {
      return " [Can Approve]";
    }
    return "";
  }

  private boolean isApprover(SuggestedReviewerInfo ri, ChangeControl cc) {
    AccountInfo ai = ri.account;
    if (ai == null) {
      return false;
    }

    final IdentifiedUser reviewer = userFactory.create(
        accountCache.get(new Account.Id(ai._accountId)).getAccount().getId());

    LabelTypes labelTypes = cc.getLabelTypes();
    if (labelTypes.byLabel(codeReviewLabelName) == null) {
      return false;
    }

    return cc.forUser(reviewer).getRange(codeReviewPermission)
        .getMax() == labelTypes.byLabel(codeReviewLabelName).getMax()
            .getValue();
  }
}
