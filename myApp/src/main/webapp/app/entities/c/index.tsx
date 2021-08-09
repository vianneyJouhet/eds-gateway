import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import C from './c';
import CDetail from './c-detail';
import CUpdate from './c-update';
import CDeleteDialog from './c-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CDetail} />
      <ErrorBoundaryRoute path={match.url} component={C} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CDeleteDialog} />
  </>
);

export default Routes;
