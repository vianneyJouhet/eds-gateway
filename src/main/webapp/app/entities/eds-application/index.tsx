import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EDSApplication from './eds-application';
import EDSApplicationDetail from './eds-application-detail';
import EDSApplicationUpdate from './eds-application-update';
import EDSApplicationDeleteDialog from './eds-application-delete-dialog';

const EDSApplicationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EDSApplication />} />
    <Route path="new" element={<EDSApplicationUpdate />} />
    <Route path=":id">
      <Route index element={<EDSApplicationDetail />} />
      <Route path="edit" element={<EDSApplicationUpdate />} />
      <Route path="delete" element={<EDSApplicationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EDSApplicationRoutes;
