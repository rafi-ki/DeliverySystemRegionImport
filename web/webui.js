/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function editRow(id)
{
    $('#' + id + '_id_input').show();
    $('#' + id + '_externalId_input').show();
    $('#' + id + '_longitude_input').show();
    $('#' + id + '_latitude_input').show();
    
    $('#' + id + '_id').hide();
    $('#' + id + '_externalId').hide();
    $('#' + id + '_longitude').hide();
    $('#' + id + '_latitude').hide();
    
    $('#edit_' + id).hide();
    $('#delete_' + id).hide();
    $('#commit_' + id).show();
    $('#cancel_' + id).show();
}

function cancelEditRow(id)
{
    $('#' + id + '_id_input').hide();
    $('#' + id + '_externalId_input').hide();
    $('#' + id + '_longitude_input').hide();
    $('#' + id + '_latitude_input').hide();
    
    $('#' + id + '_id').show();
    $('#' + id + '_externalId').show();
    $('#' + id + '_longitude').show();
    $('#' + id + '_latitude').show();
    
    $('#edit_' + id).show();
    $('#delete_' + id).show();
    $('#commit_' + id).hide();
    $('#cancel_' + id).hide();
}

function prepareAndSubmit(id, isDelete)
{
    $('#regionForm_externalId').val($('#' + id + "_externalId_input").val());
    $('#regionForm_longitude').val($('#' + id + "_longitude_input").val());
    $('#regionForm_latitude').val($('#' + id + "_latitude_input").val());
    $('#regionForm_id').val(id);
    
    if (isDelete)
        $('#regionForm_method').val('delete');
    else
        $('#regionForm_method').val('update');
    
    $('#regionForm').submit();
}