import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import D from './d';
import DDetail from './d-detail';
import DUpdate from './d-update';
import DDeleteDialog from './d-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DDetail} />
      <ErrorBoundaryRoute path={match.url} component={D} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DDeleteDialog} />
  </>
);

export default Routes;
