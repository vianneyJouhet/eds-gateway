import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import A from './a';
import ADetail from './a-detail';
import AUpdate from './a-update';
import ADeleteDialog from './a-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ADetail} />
      <ErrorBoundaryRoute path={match.url} component={A} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ADeleteDialog} />
  </>
);

export default Routes;
