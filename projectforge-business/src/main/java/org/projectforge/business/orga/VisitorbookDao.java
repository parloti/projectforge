/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.business.orga;

import java.util.ArrayList;
import java.util.List;

import org.projectforge.business.user.UserRightId;
import org.projectforge.framework.persistence.api.BaseDao;
import org.projectforge.framework.persistence.api.BaseSearchFilter;
import org.projectforge.framework.persistence.api.QueryFilter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class VisitorbookDao extends BaseDao<VisitorbookDO>
{
  public static final UserRightId USER_RIGHT_ID = UserRightId.ORGA_VISITORBOOK;

  protected VisitorbookDao()
  {
    super(VisitorbookDO.class);
    userRightId = USER_RIGHT_ID;
  }

  @Override
  public List<VisitorbookDO> getList(final BaseSearchFilter filter)
  {
    final VisitorbookFilter myFilter;
    if (filter instanceof VisitorbookFilter) {
      myFilter = (VisitorbookFilter) filter;
    } else {
      myFilter = new VisitorbookFilter(filter);
    }
    final List<VisitorbookDO> result = new ArrayList<>();
    final QueryFilter queryFilter = createQueryFilter(myFilter);
    List<VisitorbookDO> resultList = getList(queryFilter);
    resultList.forEach(vb -> {
      if (myFilter.getStartTime() != null && myFilter.getStopTime() == null) {
        vb.getTimeableAttributes().forEach(ta -> {
          if (myFilter.getStartTime().before(ta.getStartTime()) || myFilter.getStartTime().equals(ta.getStartTime())) {
            result.add(vb);
          }
        });
        return;
      }
      if (myFilter.getStartTime() == null && myFilter.getStopTime() != null) {
        vb.getTimeableAttributes().forEach(ta -> {
          if (myFilter.getStopTime().after(ta.getStartTime()) || myFilter.getStopTime().equals(ta.getStartTime())) {
            result.add(vb);
          }
        });
        return;
      }
      if (myFilter.getStartTime() != null && myFilter.getStopTime() != null) {
        vb.getTimeableAttributes().forEach(ta -> {
          if (myFilter.getStartTime().before(ta.getStartTime()) || myFilter.getStartTime().equals(ta.getStartTime())
              && myFilter.getStopTime().after(ta.getStartTime()) || myFilter.getStopTime().equals(ta.getStartTime())) {
            result.add(vb);
          }
        });
        return;
      }
      result.add(vb);
    });
    return result;
  }

  @Override
  public VisitorbookDO newInstance()
  {
    return new VisitorbookDO();
  }
}
