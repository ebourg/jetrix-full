/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.jetrix.monitor.job;

import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class TransactionalQuartzJob extends QuartzJobBean
{
    protected Logger log = Logger.getLogger(getClass().getName());

    protected final void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        ApplicationContext context = (ApplicationContext) jobExecutionContext.getMergedJobDataMap().get("applicationContext");
        SessionFactory factory = (SessionFactory) context.getBean("sessionFactory");

        Session session = SessionFactoryUtils.getSession(factory, true);

        boolean existingTransaction = SessionFactoryUtils.isSessionTransactional(session, factory);

        if (!existingTransaction)
        {
            TransactionSynchronizationManager.bindResource(factory, new SessionHolder(session));
            session.beginTransaction();
        }

        try
        {
            executeTransactional(jobExecutionContext);
            session.getTransaction().commit();
        }
        catch (Exception e)
        {
            session.getTransaction().rollback();
            if (e instanceof JobExecutionException)
            {
                throw (JobExecutionException) e;
            }
            else
            {
                throw new JobExecutionException(e);
            }
        }
        finally
        {
            if (!existingTransaction)
            {
                TransactionSynchronizationManager.unbindResource(factory);
                SessionFactoryUtils.releaseSession(session, factory);
            }
        }
    }

    protected abstract void executeTransactional(JobExecutionContext ctx) throws JobExecutionException;

}
