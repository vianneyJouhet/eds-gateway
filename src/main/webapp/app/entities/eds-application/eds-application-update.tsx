import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IEDSApplication } from 'app/shared/model/eds-application.model';
import { getEntity, updateEntity, createEntity, reset } from './eds-application.reducer';

export const EDSApplicationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const eDSApplicationEntity = useAppSelector(state => state.edsgateway.eDSApplication.entity);
  const loading = useAppSelector(state => state.edsgateway.eDSApplication.loading);
  const updating = useAppSelector(state => state.edsgateway.eDSApplication.updating);
  const updateSuccess = useAppSelector(state => state.edsgateway.eDSApplication.updateSuccess);

  const handleClose = () => {
    navigate('/eds-application');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...eDSApplicationEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...eDSApplicationEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edsGatewayApp.eDSApplication.home.createOrEditLabel" data-cy="EDSApplicationCreateUpdateHeading">
            <Translate contentKey="edsGatewayApp.eDSApplication.home.createOrEditLabel">Create or edit a EDSApplication</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="eds-application-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.name')}
                id="eds-application-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedBlobField
                label={translate('edsGatewayApp.eDSApplication.logo')}
                id="eds-application-logo"
                name="logo"
                data-cy="logo"
                isImage
                accept="image/*"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.link')}
                id="eds-application-link"
                name="link"
                data-cy="link"
                type="text"
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.description')}
                id="eds-application-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.category')}
                id="eds-application-category"
                name="category"
                data-cy="category"
                type="text"
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.authorizedRole')}
                id="eds-application-authorizedRole"
                name="authorizedRole"
                data-cy="authorizedRole"
                type="text"
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.needAuth')}
                id="eds-application-needAuth"
                name="needAuth"
                data-cy="needAuth"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edsGatewayApp.eDSApplication.defaultHidden')}
                id="eds-application-defaultHidden"
                name="defaultHidden"
                data-cy="defaultHidden"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/eds-application" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EDSApplicationUpdate;
